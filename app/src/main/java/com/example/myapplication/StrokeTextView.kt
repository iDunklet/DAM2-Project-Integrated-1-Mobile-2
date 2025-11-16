package com.example.myapplication

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class StrokeTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : AppCompatTextView(context, attrs, defStyle) {

    var strokeWidth = 10f          // Grosor del borde
    var strokeColor = Color.parseColor("#2A3A44")  // Color del borde

    override fun onDraw(canvas: Canvas) {
        val originalColor = currentTextColor

        // Dibuja el borde
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = strokeWidth
        setTextColor(strokeColor)
        super.onDraw(canvas)

        // Dibuja el texto normal encima
        paint.style = Paint.Style.FILL
        setTextColor(originalColor)
        super.onDraw(canvas)
    }
}
