package com.example.kyon

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.*
import android.media.Image
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageProxy
import com.example.kyon.Detection.Recognition
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.vision.classifier.Classifications
import org.tensorflow.lite.task.vision.classifier.ImageClassifier
import org.tensorflow.lite.task.vision.classifier.ImageClassifier.ImageClassifierOptions
import org.tensorflow.lite.task.vision.detector.Detection
import org.tensorflow.lite.task.vision.detector.ObjectDetector
import org.tensorflow.lite.task.vision.detector.ObjectDetector.ObjectDetectorOptions
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.util.ArrayList

class ImageAnalyzerTensorflow(private val mContext: Context) :
    AppCompatActivity() {
    private val detectionModelName = "Dog_Detector_metadata.tflite"
    private val assetLabelName = "label.txt"
    private val classifierModelName = "Dog_Detector_metadata.tflite"
    private val isQuant = false

    //options
    private val tfLiteOptions = Interpreter.Options()

    //Interpreter
    private var labelList: List<String>? = null
    private val imgData: ByteBuffer? = null

    //depends on size model
    private val DIM_IMG_SIZE = 320
    private val MIN_CONFIDENCE = 0.5f
    private var detection: RectF? = null

    //Detection
    private val detectorOptions = ObjectDetectorOptions.builder()
        .setMaxResults(1)
        .setScoreThreshold(0.5f)
        .build()
    var detector: ObjectDetector? = null
    var results: List<Detection>? = null
    var tensorIm: TensorImage? = null

    //Classification
    var classifierOptions = ImageClassifierOptions.builder()
        .setMaxResults(3)
        .build()
    var classifier: ImageClassifier? = null
    var result: List<Classifications>? = null

    //NOT ARRAYS
    var detectLabel: String? = null
        private set
    var detectConfidence: Float? = null
        private set
    var detectLocation: RectF? = null
        private set

    @SuppressLint("UnsafeOptInUsageError")
    fun analyzeImage(imageProxy: ImageProxy) {
        try {
            detector = ObjectDetector.createFromFileAndOptions(
                mContext,
                detectionModelName,
                detectorOptions
            )
            labelList = loadLabelList()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        @SuppressLint("UnsafeExperimentalUsageError")
        val image = imageProxy.image
        val bitmapOrig = toBitmap(image)
        val bitmap = getResizedBitmap(bitmapOrig, DIM_IMG_SIZE, DIM_IMG_SIZE)

        tensorIm = TensorImage.fromBitmap(bitmap)
        results = detector!!.detect(tensorIm)
        // run tfLite
        Log.d("CHECKPOINT", "BITMAP REACHED")
        readOutput()
    }

    private fun readOutput() {
        Log.d("CHECKPOINT", "READ OUTPUT REACHED")
        detection = null
        detectLocation = null
        detectConfidence = null
        detectLabel = null
        if (results!!.size != 0) {
            Log.d("DETECTION", "hasssssssssssssssssssssssssss detection")
            val dog = labelList!![0]
            val resultScores = results!![0].categories[0].score
            val fScore = Integer.toString((resultScores * 100).toInt())
            Log.d("DETECTION", "$dog: $fScore")
            val recognitions = ArrayList<Recognition>(1)
            detection = RectF(
                results!![0].boundingBox.left + DIM_IMG_SIZE,
                results!![0].boundingBox.top + DIM_IMG_SIZE,
                results!![0].boundingBox.right + DIM_IMG_SIZE,
                results!![0].boundingBox.bottom + DIM_IMG_SIZE
            )
            recognitions.add(Recognition("" + 0, dog, resultScores, detection!!))
            for (result in recognitions) {
                if (results!![0].boundingBox != null && result.getConfidence() >= MIN_CONFIDENCE) {
                    detectLocation = result.getLocation()
                    detectLabel = result.getTitle()
                    detectConfidence = result.getConfidence() * 100
                    Log.d("CHECKPOINT", "DETECTION SUCCESSFUL")
                }
            }
        }
    }

    private fun getResizedBitmap(
        bitmap_orig: Bitmap,
        dim_img_size_x: Int,
        dim_img_size_y: Int
    ): Bitmap {
        val width = bitmap_orig.width
        val height = bitmap_orig.height
        val scaleWidth = dim_img_size_x.toFloat() / width
        val scaleHeigth = dim_img_size_y.toFloat() / height
        val matrix = Matrix()
        matrix.postScale(scaleWidth, scaleHeigth)
        val orientation = mContext.resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            matrix.postRotate(90f)
        } else {
            matrix.postRotate(0f)
        }
        return Bitmap.createBitmap(bitmap_orig, 0, 0, width, height, matrix, false)
    }

    @Throws(IOException::class)
    private fun loadLabelList(): List<String> {
        val labelList: MutableList<String> = ArrayList()
        val reader = BufferedReader(InputStreamReader(mContext.assets.open(assetLabelName)))
        var line: String
        while (reader.readLine().also { line = it } != null) {
            labelList.add(line)
        }
        reader.close()
        return labelList
    }

    fun detectDog(selectImage: Uri?): Boolean? {
        try {
            detector = ObjectDetector.createFromFileAndOptions(
                mContext,
                detectionModelName,
                detectorOptions
            )
            labelList = loadLabelList()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        try {
            val bitmap = MediaStore.Images.Media.getBitmap(mContext.contentResolver, selectImage)
            results = detector!!.detect(TensorImage.fromBitmap(bitmap))
            return (results as MutableList<Detection>?)?.isNotEmpty()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return false
    }

        private fun toBitmap(image: Image?): Bitmap {
            Log.d("CHECKPOINT", "FUNCTION TO BITMAP")
            val planes = image!!.planes
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
            val yuvImage = YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)
            val out = ByteArrayOutputStream()
            yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 75, out)
            val imageBytes = out.toByteArray()
            return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        }

}