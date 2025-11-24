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

        try {
            // Inicializar Chaquopy
            if (!Python.isStarted()) {
                Python.start(AndroidPlatform(this))
            }

            // Obtener instancia de Python
            val python = Python.getInstance()

            // Verificar qué módulos están disponibles
            val sys = python.getModule("sys")
            Log.d("PYTHON_DEBUG", "Python path: ${sys["path"]}")

            // Listar archivos en el directorio actual
            val os = python.getModule("os")
            val files = os.callAttr("listdir", os.callAttr("getcwd"))
            Log.d("PYTHON_DEBUG", "Files in directory: $files")

            // Intentar importar tu script
            val py = python.getModule("script")
            val result: PyObject = py.callAttr("get_hello")
            val helloWorldString = result.toString()

            Log.d("PYTHON_TEST", "Result: $helloWorldString")

        } catch (e: Exception) {
            Log.e("PYTHON_ERROR", "Error: ${e.message}", e)
        }
    }
}
