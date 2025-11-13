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
import android.graphics.Color
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.Gravity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs


class Game : AppCompatActivity() {

    enum class Numeros(val valor: Int, val texto: String) {
        UNO(1, "uno"),
        DOS(2, "dos"),
        TRES(3, "tres"),
        CUATRO(4, "cuatro"),
        CINCO(5, "cinco")
    }

    private lateinit var user: User
    private var userData: UserDataGame? = null
    private var gotasCreadas = 0
    private var maxGotas = 10
    private var vidasRestantes = 3
    private var tiempoCaidaGota = 50f
    private var puntos = 0
    private var startTime: Long = 0
    private lateinit var hearts: List<ImageView>
    private lateinit var gatos: List<Int>
    private var golpesGato = 0 // 0 = happy, 1 = neutral, 2 = sad
    private lateinit var gatoView: ImageView
    private val gotasActivas = mutableListOf<Gota>()
    private lateinit var speechRecognizer: SpeechRecognizer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val buttonMike = findViewById<ImageButton>(R.id.buttonMike)
        val btnPausa = findViewById<ImageButton>(R.id.butonPausa)
        val overlay = findViewById<View>(R.id.pauseEfect)
        gatoView = findViewById(R.id.imgViewGato)

        hearts = listOf(
            findViewById(R.id.heart3),
            findViewById(R.id.heart2),
            findViewById(R.id.heart1)
        )
        gatos = listOf(
            R.drawable.neutral_cat,
            R.drawable.sad_cat
        )

        user = intent.getSerializableExtra("user") as User
        userData = buscarUserGameData()

        if (userData?.dificulty.equals("difficult")){
            maxGotas = 20;
        }


        btnPausa.setOnClickListener {
            btnPausa.isSelected = !btnPausa.isSelected
            overlay.visibility = if (btnPausa.isSelected) View.VISIBLE else View.GONE
        }

        if (checkSelfPermission(android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.RECORD_AUDIO), 101)
        }

        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            Log.e("SpeechDebug", "Speech recognition no disponible en este dispositivo")
            Toast.makeText(this, "No se puede usar reconocimiento de voz en esta tablet", Toast.LENGTH_LONG).show()
        }

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

        iniciarCicloGotas()

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101 && grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Necesitas dar permiso de micrófono para usar reconocimiento de voz", Toast.LENGTH_LONG).show()
        }
    }
    private fun iniciarCicloGotas() {
        startTime = System.currentTimeMillis()

        if (gotasCreadas <= maxGotas) {
            moverImagenVertical {
                gotasCreadas++
                iniciarCicloGotas()
            }
        }else{
            for (g in user.gameList!!){
                if (g.gameTime ==null && g.points ==null && g.errors == null){
                    g.gameTime = ((System.currentTimeMillis() - startTime) / 1000).toInt()
                    g.errors = golpesGato
                    g.points = puntos
                    g.date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(
                        Date()
                    )
                    val intent = Intent(this, GameWon::class.java)
                    intent.putExtra("user_data", user)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    private fun moverImagenVertical(onFinish: () -> Unit) {
        val zonaIzq = findViewById<ConstraintLayout>(R.id.zonaIzquierda)
        val gato = findViewById<ImageView>(R.id.imgViewGato)

        val numerosPosibles = Numeros.values()
        val numerosEnGota = List(2) { numerosPosibles.random() }

        val imagen = ImageView(this).apply {
            layoutParams = ConstraintLayout.LayoutParams(130.dpToPx(), 130.dpToPx())
            setImageResource(R.drawable.raindrop)
        }

        val textoExpresion = StrokeTextView(this).apply {
            layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            text = "${numerosEnGota[0].valor}+${numerosEnGota[1].valor}"
            textSize = 22f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }

        zonaIzq.addView(imagen)
        zonaIzq.addView(textoExpresion)

        val gota = Gota(imagen, numerosEnGota, textoExpresion)
        gotasActivas.add(gota)

        gato.post {
            val startY = -imagen.layoutParams.height.toFloat()
            val endY = gato.y + gato.height / 2f

            imagen.x = gato.x + gato.width / 2f - imagen.layoutParams.width / 2f
            imagen.y = startY

            textoExpresion.x = imagen.x + (imagen.layoutParams.width - textoExpresion.width) / 2f
            textoExpresion.y = startY + (imagen.layoutParams.height - textoExpresion.height) / 2f

            val handler = Handler(Looper.getMainLooper())
            val intervalo = 16L
            val startTime = System.currentTimeMillis()

            val runnable = object : Runnable {
                override fun run() {
                    try {
                        if (isFinishing || imagen.parent == null) return

                        val elapsed = (System.currentTimeMillis() - startTime) / 1000f
                        val desplazamiento = tiempoCaidaGota * elapsed

                        val nuevaY = startY + desplazamiento
                        imagen.y = nuevaY
                        textoExpresion.y = nuevaY + (imagen.layoutParams.height - textoExpresion.height) / 2f

                        // Colisión: centro de la gota vs parte superior del gato
                        val centroGota = nuevaY + imagen.height / 2f
                        Log.e("GameDebug", "CentroGota: $centroGota, GatoY: ${gato.y}, NuevaY: $nuevaY")

                        if (centroGota >= gato.y) {
                            Log.e("GameDebug", "Colisión detectada")
                            handler.removeCallbacks(this)
                            eliminarGota(gota)
                            actualizarGato()
                            perderVida()
                            onFinish()
                            return
                        }

                        // Si sale de pantalla
                        if (nuevaY > zonaIzq.height) {
                            Log.e("GameDebug", "Gota fuera de pantalla")
                            handler.removeCallbacks(this)
                            eliminarGota(gota)
                            onFinish()
                            return
                        }

                        handler.postDelayed(this, intervalo)
                    } catch (e: Exception) {
                        Log.e("GameDebug", "Error en Runnable de gota: ${e.message}")
                    }
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
                eliminarGota(gota)
                val puntosAGanar = if (userData?.dificulty == "difficult") 3 else 1
                puntos += puntosAGanar
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
    private fun eliminarGota(gota: Gota) {
        (gota.imagen.parent as? ConstraintLayout)?.removeView(gota.imagen)
        (gota.texto.parent as? ConstraintLayout)?.removeView(gota.texto)
        gotasActivas.remove(gota)
    }
    private fun perderVida() {
        if (vidasRestantes > 0) {
            vidasRestantes--
            hearts[vidasRestantes].setImageResource(R.drawable.heartbroken)
        }

        if (vidasRestantes == 0) {
            Log.d("Game", "Game Over")
            userData?.gameTime = ((System.currentTimeMillis() - startTime) / 1000).toInt()
            userData?.errors = golpesGato +1
            userData?.points = puntos
            userData?.date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(
                Date()
            )
            val intent = Intent(this, GameLost::class.java)
            intent.putExtra("user_data", user)
            startActivity(intent)
            finish()
        }
    }
    private fun actualizarGato() {
        if (golpesGato < gatos.size) {
            gatoView.setImageResource(gatos[golpesGato])
            golpesGato++
        }

    }

    private fun buscarUserGameData(): UserDataGame? {
        for (g in user?.gameList!!) {
            if (g.gameTime == null && g.points == null && g.errors == null) {
                return g
            }
        }
        return null
    }

}
