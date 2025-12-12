package com.example.CatPython

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.chaquo.python.PyObject
import java.io.File

class DataPython : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_python)

        // Copiar dataset siempre
        val datasetFile = File(filesDir, "dataset.json")
        assets.open("dataset.json").use { input ->
            datasetFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        // Iniciar Python
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(this))
        }

        // Ejecutar script
        val py = Python.getInstance()
        val module = py.getModule("script")
        val result = module.callAttr("create_graphs")

        if (result != null) {
            mostrarGraficos(result.asMap())
        } else {
            Toast.makeText(this, "Error al generar gráficos", Toast.LENGTH_LONG).show()
        }
    }

    private fun mostrarGraficos(results: Map<PyObject, PyObject>) {
        val imagenes = listOf(
            "graph1" to R.id.chartImagePoints,
            "graph2" to R.id.chartImageRelation,
            "graph3" to R.id.chartImageTree,
            "confusion_matrix" to R.id.chartImageConfusion,
            "hist_gameTime" to R.id.hist_gameTime,
            "hist_errors" to R.id.hist_errors,
            "heatmap_corr" to R.id.heatmap_corr,
            "predictions_pie" to R.id.predictionsPie
        )
        var cargadas = 0
        for ((clave, idVista) in imagenes) {
            val base64Str = results[PyObject.fromJava(clave)]?.toString()
            if (!base64Str.isNullOrEmpty()) {
                val bytes = Base64.decode(base64Str, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                findViewById<ImageView>(idVista).setImageBitmap(bitmap)
                cargadas++
            }
        }
        val mensaje = if (cargadas > 0) "$cargadas gráficos mostrados" else "No se pudieron cargar gráficos"
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }
}