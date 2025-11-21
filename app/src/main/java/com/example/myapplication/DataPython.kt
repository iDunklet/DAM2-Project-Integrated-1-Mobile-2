package com.example.myapplication

import android.os.Bundle
import android.util.Log
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

        // Obtener instancia de Python
        val python = Python.getInstance()
        val py = python.getModule("script")
        val result: PyObject = py.callAttr("get_hello")
        val helloWorldString = result.toString()

        Log.d("PYTHON_TEST", helloWorldString)
    }
}
