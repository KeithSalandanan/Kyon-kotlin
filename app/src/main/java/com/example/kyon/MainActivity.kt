package com.example.kyon

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import soup.neumorphism.NeumorphCardView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        setContentView(R.layout.activity_main)
        val cardClassify = findViewById<NeumorphCardView>(R.id.cardClassifyDog)
        val cardGenerate = findViewById<NeumorphCardView>(R.id.cardGenerateOffspring)
        cardClassify.setOnClickListener {
            if (hasCameraPermission()) {
                startClassify()
            } else {
                requestPermission()
            }
        }
        cardGenerate.setOnClickListener { startGenerate() }
    }

    private fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            CAMERA_PERMISSION,
            CAMERA_REQUEST_CODE
        )
    }

    private fun startClassify() {
        val intent = Intent(this, CameraActivity::class.java)
        startActivity(intent)
    }

    private fun startGenerate() {
        val intent = Intent(this, GenOffpsringActivity::class.java)
        startActivity(intent)
    }

    companion object {
        private val CAMERA_PERMISSION = arrayOf(Manifest.permission.CAMERA)
        private const val CAMERA_REQUEST_CODE = 10
    }
}