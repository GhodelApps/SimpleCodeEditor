package com.sungbin.texteditor.library

import android.content.Context
import android.graphics.*
import android.text.Selection
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.view.Gravity
import android.widget.EditText
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.doAfterTextChanged
import com.sungbin.texteditor.library.util.CodeHighlighter
import com.sungbin.texteditor.library.util.EdittextHistoryManager
import java.util.*
import kotlin.math.log10
import kotlin.properties.Delegates

class SimpleCodeEditor : AppCompatEditText {
    private var dp by Delegates.notNull<Int>()
    private var rect = Rect()
    private var lineRect = Rect()
    private var lineNumberPaint = Paint()
    private var linePaint = Paint()
    private var highlightPaint = Paint()
    private var enableHorizontallyScroll = false
    private var applyHighlight = true
    private lateinit var historyManager: EdittextHistoryManager

    private var reservedColor = Color.argb(255, 21, 101, 192)
    private var numberColor = Color.argb(255, 191, 54, 12)
    private var stringColor = Color.argb(255, 255, 160, 0)
    private var annotationColor = Color.argb(255, 139, 195, 74)

    var highlighter = CodeHighlighter(
        reservedColor, numberColor,
        stringColor, annotationColor
    )

    /**
     * Create a new SimpleCodeEditor.
     * @param context current activity
     */
    constructor(context: Context) : super(context) {
        SimpleCodeEditor(context, null)
    }

    /**
     * Constructor for inflation from XML layout
     * @param context current activity
     * @param attrs provided by layout
     */
    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        val attr = context.obtainStyledAttributes(
            attrs,
            R.styleable.SimpleCodeEditor,
            0,
            0
        )
        val lineNumberColor = attr.getColor(
            R.styleable.SimpleCodeEditor_sce_lineNumberColor,
            Color.BLACK
        )
        val lineColor = attr.getColor(
            R.styleable.SimpleCodeEditor_sce_lineNumberColor,
            lineNumberColor
        )
        val lineTextSize = attr.getInt(
            R.styleable.SimpleCodeEditor_sce_lineNumberTextSize,
            13
        )
        val selectLineColor = attr.getColor(
            R.styleable.SimpleCodeEditor_sce_focusLineColor,
            Color.CYAN
        )
        reservedColor = attr.getColor(
            R.styleable.SimpleCodeEditor_sce_reservedColor,
            this.reservedColor
        )
        numberColor = attr.getColor(
            R.styleable.SimpleCodeEditor_sce_numberColor,
            this.numberColor
        )
        stringColor = attr.getColor(
            R.styleable.SimpleCodeEditor_sce_stringColor,
            this.stringColor
        )
        enableHorizontallyScroll = attr.getBoolean(
            R.styleable.SimpleCodeEditor_sce_enableHorizontallyScroll,
            this.enableHorizontallyScroll
        )
        annotationColor = attr.getColor(
            R.styleable.SimpleCodeEditor_sce_annotationColor,
            this.annotationColor
        )
        applyHighlight = attr.getBoolean(
            R.styleable.SimpleCodeEditor_sce_applyHighlighter,
            this.applyHighlight
        )
        highlighter = CodeHighlighter(
            reservedColor, numberColor,
            stringColor, annotationColor
        )
        attr.recycle()
        dp = context.resources.displayMetrics.density.toInt()
        gravity = Gravity.TOP or Gravity.START
        setHorizontallyScrolling(enableHorizontallyScroll)
        textSize = 14f
        if (applyHighlight) {
            doAfterTextChanged {
                try {
                    highlighter.apply(it ?: SpannableStringBuilder(""))
                } catch (ignored: Exception) {
                }
            }
        }
        setTextColor(Color.BLACK)
        lineNumberPaint.run {
            textSize = dp * lineTextSize.toFloat()
            color = lineNumberColor
            typeface = Typeface.MONOSPACE
        }
        linePaint.color = lineColor
        highlightPaint.run {
            color = selectLineColor
            alpha = 64
        }
        viewTreeObserver.addOnDrawListener {
            invalidate()
        }
        historyManager = EdittextHistoryManager(this)
    }

    override fun onDraw(canvas: Canvas) {
        try {
            val digits = log10(lineCount.toDouble()).toInt()
            val textWidth = lineNumberPaint.measureText(lineCount.toString()).toInt()
            val selectedLine = selectedLine
            var line = 1
            getDrawingRect(rect)
            for (i in 1..lineCount) {
                val baseline = getLineBounds(i - 1, lineRect)
                val spaceCount = digits - log10(i.toDouble()).toInt()
                if (!enableHorizontallyScroll) {
                    if (i == 1) {
                        canvas.drawText(
                            "${getSpace(spaceCount)}$line",
                            rect.left + dp * 4.toFloat(),
                            baseline.toFloat(),
                            lineNumberPaint
                        )
                        line++
                    }
                    if (i > 1 && text!![layout.getLineStart(i - 1) - 1] == '\n') {
                        canvas.drawText(
                            "${getSpace(spaceCount)}$line",
                            rect.left + dp * 4.toFloat(),
                            baseline.toFloat(),
                            lineNumberPaint
                        )
                        line++
                    }
                } else {
                    canvas.drawText(
                        "${getSpace(spaceCount)}$i",
                        rect.left + dp * 4.toFloat(),
                        baseline.toFloat(),
                        lineNumberPaint
                    )
                }
                if (i - 1 == selectedLine) {
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

            setPadding(textWidth + dp * 12, 0, 0, 0)
            super.onDraw(canvas)
        } catch (ignored: Exception) {
            super.onDraw(canvas)
        }
    }

    fun undo() {
        historyManager.undo()
    }

    fun redo() {
        historyManager.redo()
    }

    fun findText(string: String, ignoreUpper: Boolean = false): ArrayList<ArrayList<Int>> {
        val lines = text.toString().split("\n")
        val array = ArrayList<ArrayList<Int>>()
        for ((index, text) in lines.withIndex()) {
            if (ignoreUpper) {
                text.toLowerCase(Locale.getDefault())
                string.toLowerCase(Locale.getDefault())
            }
            if (text.contains(string)) {
                array.add(arrayListOf(index, text.indexOf(string)))
            }
        }
        return array
    }

    private val selectedLine: Int
        get() {
            val selectionStart = Selection.getSelectionStart(text)
            return if (selectionStart > -1) {
                layout.getLineForOffset(selectionStart)
            } else -1
        }

    private fun getSpace(count: Int): String {
        val result = StringBuilder()
        for (i in 0 until count) result.append(" ")
        return result.toString()
    }

    private fun EditText.removeAllSpan() {
        val removeSpans =
            this.text?.getSpans(0, this.text?.length ?: 0, ForegroundColorSpan::class.java)
        for (span in removeSpans ?: return) {
            this.text?.removeSpan(span)
        }
    }

    fun applyHighlight(power: Boolean) {
        applyHighlight = power
        if (power) {
            doAfterTextChanged {
                try {
                    highlighter.apply(it ?: SpannableStringBuilder(""))
                } catch (ignored: Exception) {
                }
            }
        } else {
            this.removeAllSpan()
            doAfterTextChanged {}
        }
    }
}