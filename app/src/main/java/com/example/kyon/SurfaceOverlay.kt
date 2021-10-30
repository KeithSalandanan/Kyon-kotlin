package com.example.kyon

import android.annotation.SuppressLint
import android.graphics.*
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import kotlinx.android.synthetic.main.activity_camera.view.*
import org.tensorflow.lite.task.vision.detector.Detection

@SuppressLint("ViewConstructor")
class SurfaceOverlay(surfaceView : SurfaceView) :SurfaceView(surfaceView.context),SurfaceHolder.Callback {

    init{
        surfaceView.holder.addCallback(this)
        surfaceView.setZOrderOnTop(true)
    }

    private var surfaceHolder: SurfaceHolder = surfaceView.holder
    private val paint = Paint()
    private val pathColor = listOf(Color.GREEN)



    override fun surfaceCreated(holder: SurfaceHolder) {
        surfaceHolder.setFormat(PixelFormat.RGBA_8888)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {

    }

    val dog = arrayOf("Dog")
    fun draw(detectedDogs: List<Detection>){
        val canvas:Canvas? = surfaceHolder.lockCanvas()


        canvas?.drawColor(0, PorterDuff.Mode.CLEAR)

            detectedDogs.mapIndexed{i,detectedDogs ->
                paint.apply {
                    color = pathColor[i]
                    style = Paint.Style.STROKE
                    strokeWidth = 5f
                    isAntiAlias = false
                }

                canvas?.drawRoundRect(detectedDogs.boundingBox.left,detectedDogs.boundingBox.top,detectedDogs.boundingBox.right+320,detectedDogs.boundingBox.bottom+320,15f,15f,paint)

                val bounds = "%.2f,%.2f,%.2f,%.2f".format(detectedDogs.boundingBox.top,detectedDogs.boundingBox.left,detectedDogs.boundingBox.right,detectedDogs.boundingBox.bottom)
                Log.d("BOUNDS",bounds)

                paint.apply {
                    style = Paint.Style.FILL
                    isAntiAlias = true
                    textSize = 50f
                }
                val text = dog[detectedDogs.categories.first().index] + " " + "%.1f".format(detectedDogs.categories.first().score * 100)+"%"
                canvas?.drawText(text,detectedDogs.boundingBox.left,detectedDogs.boundingBox.bottom+365,paint);

            }


        surfaceHolder.unlockCanvasAndPost(canvas?:return)
    }
}