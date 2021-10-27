package com.example.kyon

import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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
            Toast.makeText(this, "Nag Classify", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            Toast.makeText(this, "Ayaw mag classify", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }

    }

    private fun loadImage() {
            myUri = Uri.parse(intent.getStringExtra("strImg"))
            ImgCaptured.setImageURI(myUri)
    }



}