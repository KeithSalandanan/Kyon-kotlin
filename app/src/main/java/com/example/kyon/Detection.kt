package com.example.kyon

import android.graphics.RectF

interface Detection {
    class Recognition(
        private val id: String,
        private val title: String,
        private val confidence: Float,
        private val location: RectF
    ) {
        fun getTitle(): String {
            return title
        }
        fun getConfidence(): Float {
            return confidence
        }

        fun getLocation(): RectF {
            return RectF(location)
        }

    }
}