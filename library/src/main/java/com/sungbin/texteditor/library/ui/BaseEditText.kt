package com.sungbin.texteditor.library.ui

import android.content.Context
import android.graphics.*
import android.text.Selection
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.widget.EditText
import android.widget.ScrollView
import kotlin.math.log10

class BaseEditText(context: Context, attrs: AttributeSet?) :
    ScrollView(context, attrs) {

    private val dp: Int
    private val rect: Rect
    private val lineRect: Rect
    private val lineNumberPaint: Paint
    private val linePaint: Paint
    private val highlightPaint: Paint
    private var currentLine = 0
    private var num = 1
    private var lastLineNumber = 0

    val editor: EditText
    var lineNumberColor = Color.parseColor("#909090")
    var lineBackgroundColor = Color.parseColor("#42A5F5")

    override fun onDraw(canvas: Canvas) {
        val lineCount = editor.lineCount
        val digits = log10(lineCount.toDouble()).toInt()
        val textWidth = lineNumberPaint.measureText(lineCount.toString()).toInt()
        val selectedLine = selectedLine
        getDrawingRect(rect)

        for (i in 0 until lineCount) {
            val baseline = editor.getLineBounds(i, lineRect)
            val spaceCount = digits - log10(i.toDouble()).toInt()
            if (i == 0 || editor.text[editor.layout.getLineStart(i) - 1] == '\n') {
                canvas.drawText(
                    getSpace(spaceCount) + num,
                    rect.left + dp * 4.toFloat(),
                    baseline.toFloat(),
                    lineNumberPaint
                )
                if(lastLineNumber < i) {
                    lastLineNumber = i
                    num++
                }
            }
            if (i == selectedLine) {
                lineRect.left += rect.left - dp * 4
                canvas.drawRect(lineRect, highlightPaint)
            }
        }
        canvas.drawLine(
            rect.left + textWidth + (dp * 8).toFloat(),
            rect.top.toFloat(),
            rect.left + textWidth + (dp * 8).toFloat(),
            rect.bottom.toFloat(),
            linePaint
        )
        editor.setPadding(textWidth + dp * 12, 0, 0, 0)
        super.onDraw(canvas)
    }

    private val selectedLine: Int
        get() {
            val selectionStart = Selection.getSelectionStart(editor.text)
            val layout = editor.layout
            return if (selectionStart != -1) {
                layout.getLineForOffset(selectionStart)
            } else -1
        }

    private fun getSpace(count: Int): String {
        val result = StringBuilder()
        for (i in 0 until count) result.append(" ")
        return result.toString()
    }

    init {
        isFillViewport = true
        dp = context.resources.displayMetrics.density.toInt()
        editor = EditText(context)
        editor.background = null
        editor.gravity = Gravity.TOP or Gravity.START
        //editor.setHorizontallyScrolling(true)
        editor.textSize = 14f
        editor.setTextColor(Color.parseColor("#000000"))
        editor.typeface = Typeface.MONOSPACE
        editor.viewTreeObserver.addOnDrawListener {
            invalidate()
        }
        addView(editor)

        rect = Rect()
        lineRect = Rect()
        linePaint = Paint()
        highlightPaint = Paint()
        lineNumberPaint = Paint()

        linePaint.color = lineNumberColor
        lineNumberPaint.textSize = dp * 10.toFloat()
        lineNumberPaint.color = lineNumberColor
        lineNumberPaint.typeface = Typeface.MONOSPACE

        highlightPaint.color = lineBackgroundColor
        highlightPaint.alpha = 64

        viewTreeObserver.addOnScrollChangedListener {
            currentLine = editor.lineCount * scrollY / getChildAt(0).height
            invalidate()
        }
    }
}