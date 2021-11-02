package com.example.kyon


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_camera.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.AbstractExecutorService
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : AppCompatActivity() {
    private var imageCapture:ImageCapture? = null
    private var selectImage:Uri? = null
    private lateinit var outputDirectory:File
    lateinit var viewFinder:PreviewView
    lateinit var cameraExecutor :ExecutorService

    private var mPhotoPath: String? = null
    private var mSwitchCameraState = CameraSelector.LENS_FACING_BACK
    private var mZoomState = 0.0f
    val imagePath = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )


        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }


        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()



        //initiate zoom level otherwise is not in the foreground -> bug?
        zoomTextView.setText(mZoomState.toString() + "x")
        zoomTextView.setVisibility(View.INVISIBLE)
        viewFinder = findViewById(R.id.viewFinder)


        //Listeners
        switchCameraButton.setOnClickListener { switchCamera() }
        btnClose.setOnClickListener { closeCamera() }
        takePictureButton.setOnClickListener { takePhoto() }
        openGalleryButton.setOnClickListener { openGallery() }
        supportActionBar?.hide()

    }
    companion object {
        private const val TAG = "CameraXBasic"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    private fun closeCamera() {
//        val Intent = Intent(this, MainActivity::class.java)
//        startActivity(intent)
        finish()
    }
    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() } }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("photoPath", mPhotoPath)
        outState.putInt("cameraState", mSwitchCameraState)
        outState.putFloat("zoomState", mZoomState)
        super.onSaveInstanceState(outState)
    }
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        mPhotoPath = savedInstanceState.getString("photoPath")
        mSwitchCameraState = savedInstanceState.getInt("cameraState")
        mZoomState = savedInstanceState.getFloat("zoomState")
        super.onRestoreInstanceState(savedInstanceState)
    }


    private fun startCamera(){
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .build()

            val imageAnalyzer = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, ImageAnalyzer(this,resultView,takePictureButton,SignalText))
                }

            // Select back camera as a default
            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(mSwitchCameraState)
                .build()

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
               val camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture, imageAnalyzer)


                //control zoom
                val cameraControl = camera.cameraControl
                controlZoom(cameraControl)
            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }
    private fun takePhoto(){
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time-stamped output file to hold the image
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(FILENAME_FORMAT, Locale.US
            ).format(System.currentTimeMillis()) + ".jpg")

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        val intent = Intent(this,ClassificationActivity::class.java)
        imageCapture.takePicture(
            outputOptions, ContextCompat.getMainExecutor(this), object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    val msg = "Photo capture succeeded: $savedUri"
//                    Toast.makeText(baseContext, msg, Toast.LENGTH_LONG).show()
                    Log.d(TAG, msg)

                    intent.putExtra("imagePath",savedUri.toString())
                    startActivity(intent)

                }
            })
    }
    private fun controlZoom(cameraControl: CameraControl) {
        cameraControl.setLinearZoom(mZoomState)
        zoomSliderSeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                mZoomState = i / "4".toFloat()
                cameraControl.setLinearZoom(mZoomState)
                zoomTextView.setText((mZoomState * 8).toString() + "x")
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                zoomTextView.setVisibility(View.VISIBLE)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                zoomTextView.postDelayed(
                    Runnable { zoomTextView.setVisibility(View.INVISIBLE) },
                    3000
                )
            }
        })
    }
    private fun switchCamera() {
        if (!switchCameraButton.isChecked()) {
            mSwitchCameraState = CameraSelector.LENS_FACING_BACK
            startCamera()
        } else {
            mSwitchCameraState = CameraSelector.LENS_FACING_FRONT
            startCamera()
        }
    }

    //Opens Gallery
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 3)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, requestCode, data)
        if (resultCode == RESULT_OK && data != null) {
            selectImage = data.data
            val tfAnalyze = ImageAnalyzerTensorflow(this)
            if (tfAnalyze.detectDog(selectImage)!!) {
                Toast.makeText(this, "Dogs Detected", Toast.LENGTH_LONG).show()
                val intent = Intent(this, ClassificationActivity::class.java)
                intent.putExtra("imagePath", selectImage.toString())
                startActivity(intent)
            } else {
                Toast.makeText(this, "No Dogs Detected", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all{
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }



}

