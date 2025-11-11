package com.example.myapplication

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import android.content.res.Resources
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer

class Game : AppCompatActivity() {

    enum class Numeros(val valor: Int, val texto: String) {
        UNO(1, "uno"),
        DOS(2, "dos"),
        TRES(3, "tres"),
        CUATRO(4, "cuatro"),
        CINCO(5, "cinco")
    }

    private val gotasActivas = mutableListOf<Gota>()
    private lateinit var speechRecognizer: SpeechRecognizer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val buttonMike = findViewById<ImageButton>(R.id.buttonMike)
        val btnPausa = findViewById<ImageButton>(R.id.butonPausa)
        val overlay = findViewById<View>(R.id.pauseEfect)

        btnPausa.setOnClickListener {
            btnPausa.isSelected = !btnPausa.isSelected
            overlay.visibility = if (btnPausa.isSelected) View.VISIBLE else View.GONE
        }

        // Solicitar permiso de micrófono si no está dado
        if (checkSelfPermission(android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.RECORD_AUDIO), 101)
        }

        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            Log.e("SpeechDebug", "Speech recognition no disponible en este dispositivo")
            Toast.makeText(this, "No se puede usar reconocimiento de voz en esta tablet", Toast.LENGTH_LONG).show()
        }// Inicializar SpeechRecognizer

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        val recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES")
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
        }

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                Log.d("SpeechDebug", "SpeechRecognizer: listo para escuchar")
            }

            override fun onBeginningOfSpeech() {
                Log.d("SpeechDebug", "SpeechRecognizer: comenzó a hablar")
            }

            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {
                Log.d("SpeechDebug", "SpeechRecognizer: terminó de hablar")
            }

            override fun onError(error: Int) {
                val mensaje = when (error) {
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "ERROR_NETWORK_TIMEOUT"
                    SpeechRecognizer.ERROR_NETWORK -> "ERROR_NETWORK"
                    SpeechRecognizer.ERROR_AUDIO -> "ERROR_AUDIO"
                    SpeechRecognizer.ERROR_SERVER -> "ERROR_SERVER"
                    SpeechRecognizer.ERROR_CLIENT -> "ERROR_CLIENT"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "ERROR_SPEECH_TIMEOUT"
                    SpeechRecognizer.ERROR_NO_MATCH -> "ERROR_NO_MATCH"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "ERROR_RECOGNIZER_BUSY"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "ERROR_INSUFFICIENT_PERMISSIONS"
                    else -> "ERROR_DESCONOCIDO: $error"
                }
                Log.d("SpeechDebug", "SpeechRecognizer Error: $mensaje")
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                Log.d("SpeechDebug", "SpeechRecognizer - onResults: $matches")

                if (!matches.isNullOrEmpty()) {
                    val textoDicho = matches[0].trim().lowercase()
                    Log.d("SpeechDebug", "Texto reconocido: $textoDicho")
                    val numero = convertirTextoANumero(textoDicho)
                    Log.d("SpeechDebug", "Número convertido: $numero")
                    if (numero != null) {
                        verificarGotasPorNumero(numero)
                    }
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                Log.d("SpeechDebug", "SpeechRecognizer - resultados parciales: $partialResults")
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        buttonMike.setOnClickListener {
            speechRecognizer.startListening(recognizerIntent)
        }

        // Iniciar movimiento de gotas
        moverImagenVertical()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101 && grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Necesitas dar permiso de micrófono para usar reconocimiento de voz", Toast.LENGTH_LONG).show()
        }
    }

    private fun moverImagenVertical() {
        val zonaIzq = findViewById<ConstraintLayout>(R.id.zonaIzquierda)
        val gato = findViewById<ImageView>(R.id.imgViewGato)
        val heart1 = findViewById<ImageView>(R.id.heart1)

        val numerosPosibles = Numeros.values()
        val numerosEnGota = List(2) { numerosPosibles.random() }

        val imagen = ImageView(this).apply {
            layoutParams = ConstraintLayout.LayoutParams(130.dpToPx(), 130.dpToPx())
            setImageResource(R.drawable.raindrop)
        }

        val textoExpresion = android.widget.TextView(this).apply {
            layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            text = "${numerosEnGota[0].valor}+${numerosEnGota[1].valor}"
            textSize = 20f
            setTextColor(android.graphics.Color.WHITE)
            gravity = android.view.Gravity.CENTER
        }

        zonaIzq.addView(imagen)
        zonaIzq.addView(textoExpresion)

        val gota = Gota(imagen, numerosEnGota, textoExpresion)
        gotasActivas.add(gota)

        gato.post {
            imagen.x = gato.x + gato.width / 2 - imagen.layoutParams.width / 2
            imagen.y = gato.y
            textoExpresion.x = imagen.x + imagen.layoutParams.width / 2 - textoExpresion.width / 2
            textoExpresion.y = imagen.y + imagen.layoutParams.height / 2 - textoExpresion.height / 2

            val handler = Handler(Looper.getMainLooper())
            val velocidad = 50f
            val intervalo = 16L
            val startTime = System.currentTimeMillis()

            val runnable = object : Runnable {
                override fun run() {
                    val elapsed = (System.currentTimeMillis() - startTime) / 1000f
                    val desplazamiento = velocidad * elapsed
                    imagen.translationY = desplazamiento
                    textoExpresion.translationY = desplazamiento

                    if (Math.abs(imagen.y - gato.y) < 80) {
                        heart1.setImageResource(R.drawable.heartbroken)
                        gato.setImageResource(R.drawable.neutral_cat)
                        (imagen.parent as? ConstraintLayout)?.removeView(imagen)
                        (textoExpresion.parent as? ConstraintLayout)?.removeView(textoExpresion)
                        gotasActivas.remove(gota)
                        handler.removeCallbacks(this)
                        return
                    }

                    handler.postDelayed(this, intervalo)
                }
            }

            handler.post(runnable)
        }
    }

    data class Gota(val imagen: ImageView, val numeros: List<Numeros>, val texto: android.widget.TextView)

    private fun verificarGotasPorNumero(numero: Int) {
        val iterator = gotasActivas.iterator()
        while (iterator.hasNext()) {
            val gota = iterator.next()
            if (verificarNumero(numero, gota.numeros)) {
                (gota.imagen.parent as? ConstraintLayout)?.removeView(gota.imagen)
                iterator.remove()
            }
        }
    }

    private fun convertirTextoANumero(texto: String): Int? {
        return when (texto) {
            "uno" -> 1
            "dos" -> 2
            "tres" -> 3
            "cuatro" -> 4
            "cinco" -> 5
            "seis" -> 6
            "siete" -> 7
            "ocho" -> 8
            "nueve" -> 9
            "diez" -> 10
            else -> texto.toIntOrNull()
        }
    }

    fun sumarNumeros(vararg numeros: Numeros): Int = numeros.sumOf { it.valor }

    fun verificarNumero(numeroSpeech: Int, numerosEnGota: List<Numeros>): Boolean {
        return numeroSpeech == sumarNumeros(*numerosEnGota.toTypedArray())
    }

    private fun Int.dpToPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
}
