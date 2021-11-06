package com.example.kyon

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_classification.*
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.vision.classifier.Classifications
import org.tensorflow.lite.task.vision.classifier.ImageClassifier
import org.tensorflow.lite.task.vision.classifier.ImageClassifier.ImageClassifierOptions
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class ClassificationActivity : AppCompatActivity() {

    private lateinit var myUri: Uri

    lateinit var classifier: ImageClassifier
    var options = ImageClassifierOptions.builder()
        .setMaxResults(3)
        .build()


    private lateinit var result: List<Classifications>
    private lateinit var bitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_classification)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )


        loadImage()

        classifier = ImageClassifier.createFromFileAndOptions(
            this,
            "Dog_Classifier_metadata.tflite",
            options
        )

        try {
            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, myUri)
            val result = classifier.classify(TensorImage.fromBitmap(bitmap))
            var str = ""
            for (c in result) {
                for (cat in c.categories) {
                    str += String.format("%.1f%s %s \n", cat.score * 100, "%", cat.label)
                }
                txtResults.text = str
            }
        } catch (e: IOException) {
            Toast.makeText(this, "Cannot Classify", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
        supportActionBar?.hide()


        saveImg.setOnClickListener { save() }
        confirmBtn.setOnClickListener { ok() }
    }

    private fun loadImage() {
        myUri = Uri.parse(intent.getStringExtra("imagePath"))
        ImgCaptured.setImageURI(myUri)
    }

    private fun save() {

        val view = window.decorView.rootView
        view.isDrawingCacheEnabled = true
        view.buildDrawingCache(true)
        val bmp = Bitmap.createBitmap(view.drawingCache)
        view.isDrawingCacheEnabled = false


        val file: File = File(getOutputDirectory(), "Kyon" + System.currentTimeMillis() + ".png")
        try {
            val fos: FileOutputStream = FileOutputStream(file)
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.flush()
            fos.close()

            MediaStore.Images.Media.insertImage(this.contentResolver,file.absolutePath,file.name,null)
            this.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,Uri.fromFile(file)))
            Toast.makeText(this, "Image Saved", Toast.LENGTH_SHORT).show()
        } catch (e: FileNotFoundException) {
            Toast.makeText(this, "Cannot Save Image", Toast.LENGTH_SHORT).show()
        }



    }

    private fun ok() {
        finish()
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() } }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }
}



