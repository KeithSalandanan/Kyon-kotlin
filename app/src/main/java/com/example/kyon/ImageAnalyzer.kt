package com.example.kyon

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.*
import android.media.Image
import android.util.Log
import android.view.SurfaceView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_camera.view.*
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.vision.detector.Detection
import org.tensorflow.lite.task.vision.detector.ObjectDetector
import java.io.ByteArrayOutputStream

class ImageAnalyzer(context : Context, surface:SurfaceView, shutter: Button,signal:TextView) : ImageAnalysis.Analyzer{

    val options: ObjectDetector.ObjectDetectorOptions = ObjectDetector.ObjectDetectorOptions.builder()
        .setMaxResults(1)
        .setScoreThreshold(0.5f)
        .build()
    private var context:Context = context
    private var shutter =shutter
    private var signal = signal
    val box = SurfaceOverlay(surface)
    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val frame = imageProxy.image
        val bitmap = frame?.toBitmap()
        val tensorIm = TensorImage.fromBitmap(bitmap)
        val detector: ObjectDetector = ObjectDetector.createFromFileAndOptions(context,"Dog_Detector_metadata.tflite",options)
        val results:List<Detection> = detector.detect(tensorIm)
        box.draw(results)
        if(results.isEmpty()){
            (context as Activity).runOnUiThread(java.lang.Runnable {
                shutter.isEnabled = false
                shutter.background = ContextCompat.getDrawable(context,R.drawable.shape_button_pressed)
                signal.text = "No Dogs Detected"
            })


        }else{
            (context as Activity).runOnUiThread(java.lang.Runnable {
                shutter.isEnabled = true
                shutter.background = ContextCompat.getDrawable(context,R.drawable.shape_button_unpressed)
                signal.text = "Take a Picture Now"
            })

        }

        imageProxy.close()
    }
    fun Image.toBitmap(): Bitmap {
        val yBuffer = planes[0].buffer
        val uBuffer = planes[1].buffer
        val vBuffer = planes[2].buffer
        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()
        val nv21 = ByteArray(ySize + uSize + vSize)
        //U and V are swapped
        yBuffer[nv21, 0, ySize]
        vBuffer[nv21, ySize, vSize]
        uBuffer[nv21, ySize + vSize, uSize]
        val yuvImage = YuvImage(nv21, ImageFormat.NV21, this.width, this.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 75, out)
        val imageBytes = out.toByteArray()
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

}