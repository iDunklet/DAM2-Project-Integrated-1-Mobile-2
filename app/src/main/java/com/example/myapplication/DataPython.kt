package com.example.myapplication

import android.os.Bundle
import android.util.Base64
import android.graphics.BitmapFactory
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import java.io.File
import java.io.FileOutputStream

class DataPython : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_python)

        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(this))
        }

        val py = Python.getInstance()
        val pythonFile = py.getModule("script")

        // Si dataset.json está en la misma carpeta que script.py
        // Chaquopy debería poder encontrarlo automáticamente
        val result = pythonFile.callAttr("returnGraphs")

        val imagePoints = findViewById<ImageView>(R.id.chartImagePoints)
        val imageRelation = findViewById<ImageView>(R.id.chartImageRelation)
        val imageTree = findViewById<ImageView>(R.id.chartImageTree)

        fun setImage(base64Str: String?, imageView: ImageView) {
            if (base64Str != null) {
                val imageBytes = Base64.decode(base64Str, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                imageView.setImageBitmap(bitmap)
            }
        }

        setImage(result?.get("points_per_player")?.toString(), imagePoints)
        setImage(result?.get("scatter_gameTime_points")?.toString(), imageRelation)
        setImage(result?.get("decision_tree")?.toString(), imageTree)
    }
}