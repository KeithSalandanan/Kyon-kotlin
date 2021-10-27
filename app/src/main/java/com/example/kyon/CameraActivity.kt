package com.example.kyon


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_camera.*

class CameraActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)


        btnClose.setOnClickListener { closeCamera() }

    }

    private fun closeCamera() {
        val Intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}

