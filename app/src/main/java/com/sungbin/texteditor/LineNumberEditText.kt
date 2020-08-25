package com.sungbin.texteditor

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import kotlin.math.log10

class LineNumberEditText(context: Context, attrs: AttributeSet?) : AppCompatEditText(
    context, attrs
) {
    private val rect = Rect()
    private val paint = Paint()
    private val linePaint = Paint().apply {
        color = Color.BLACK
    }
    private val dp = context.resources.displayMetrics.density.toInt()
    private val lineNumberPaint = Paint().apply {
        textSize = dp * 10.toFloat()
        color = Color.BLACK
        typeface = Typeface.MONOSPACE
    }

    private fun getSpace(count: Int): String {
        val result = StringBuilder()
        for (i in 0 until count) result.append(" ")
        return result.toString()
    }

    override fun onDraw(canvas: Canvas) {
        val textWidth = lineNumberPaint.measureText(lineCount.toString()).toInt()
        val lineCount = lineCount
        var line = 1
        getDrawingRect(rect)
        for (i in 0 until lineCount) {
            val lineBound = getLineBounds(i, null).toFloat()
            if (i != 0 && text?.get(layout.getLineStart(i) - 1) != '\n') continue
            val digits = log10(lineCount.toDouble()).toInt()
            val spaceCount = digits - log10(i.toDouble()).toInt()
            canvas.drawText(
                "${getSpace(spaceCount)}$i",
                rect.left + dp * 4.toFloat(),
                lineBound,
                paint
            )
            line++
        }

        canvas.drawLine(
            rect.left + textWidth + (dp * 8).toFloat(),
            rect.top.toFloat(),
            rect.left + textWidth + (dp * 8).toFloat(),
            rect.bottom.toFloat(),
            linePaint
        )
        setPadding(textWidth + dp * 12, 0, 0, 0)
        super.onDraw(canvas)
    }

    init {
        paint.style = Paint.Style.FILL
        paint.color = Color.BLACK
        paint.textSize = 40f
    }
}