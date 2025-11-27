package com.example.myapplication

import android.os.Bundle
import android.util.Base64
import android.graphics.BitmapFactory
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform

class DataPython : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_python)

        // Inicializar Chaquopy
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(this))
        }

        val py = Python.getInstance()
        val module = py.getModule("script")

        // Llamar a la función train y obtener el diccionario de gráficos


        val result = module.callAttr("train", filesDir.absolutePath).asMap()

        val imageView1 = findViewById<ImageView>(R.id.chartImage1)
        val imageView2 = findViewById<ImageView>(R.id.chartImage2)
        val imageView3 = findViewById<ImageView>(R.id.chartImage3)
        val imageView4 = findViewById<ImageView>(R.id.chartImage4)
        val imageView5 = findViewById<ImageView>(R.id.chartImage5)

        fun setImage(base64Str: String?, imageView: ImageView) {
            if (base64Str != null) {
                val imageBytes = Base64.decode(base64Str, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                imageView.setImageBitmap(bitmap)
            }
        }

// Ojo: las claves son PyObject, así que hay que convertirlas a String
        setImage(result[PyObject.fromJava("hist_gameTime")]?.toString(), imageView1)
        setImage(result[PyObject.fromJava("points_per_player")]?.toString(), imageView2)
        setImage(result[PyObject.fromJava("errors_per_difficulty")]?.toString(), imageView3)
        setImage(result[PyObject.fromJava("reaction_per_player")]?.toString(), imageView4)
        setImage(result[PyObject.fromJava("scatter_gameTime_points")]?.toString(), imageView5)

    }
}