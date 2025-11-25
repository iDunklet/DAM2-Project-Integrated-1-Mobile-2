package com.example.myapplication

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import java.io.File

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

        val result = module.callAttr("train", filesDir.absolutePath)
        Log.d("DataPython", result.toString())


    }
}
