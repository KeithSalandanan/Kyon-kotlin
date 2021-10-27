package com.example.kyon

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.graphics.*
import android.widget.ImageView

class BoundingBox(activity: Activity, context: Context) {
    private val boundingBoxContainer: ImageView
    private val ContainerWidth: Int
    private val ContainerHeight: Int
    private var scaleW = 0f
    private var scaleH = 0f
    fun noBoundingBox() {
        boundingBoxContainer.setImageBitmap(null)
    }

    fun drawSingleBox(label: String, confidence: Float, location: RectF) {
        val bitmap = Bitmap.createBitmap(
            ContainerWidth,
            ContainerHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        val paintRect = Paint()
        paintRect.style = Paint.Style.STROKE
        paintRect.color = Color.GREEN
        paintRect.strokeWidth = 10f
        val paintText = Paint()
        paintText.color = Color.GREEN
        paintText.strokeWidth = 10f
        paintText.textSize = 100f
        canvas.drawRoundRect(
            location.left,
            location.top,
            location.right,
            location.bottom,
            50.0f,
            50.0f,
            paintRect
        )
        canvas.drawText(
            "$label: $confidence",
            location.left,
            location.top,
            paintText
        )
        boundingBoxContainer.setImageBitmap(bitmap)
    }

    init {
        boundingBoxContainer = activity.findViewById(R.id.boundingBox)
        ContainerWidth = boundingBoxContainer.width
        ContainerHeight = boundingBoxContainer.height
        val orientation = context.resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            scaleW = 4.8f
            scaleH = 9.63f
        } else {
            scaleW = 9.63f
            scaleH = 4.8f
        }
    }
}
