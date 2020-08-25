package com.sungbin.texteditor.library.ui

import android.content.Context
import android.graphics.*
import android.text.Selection
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.doAfterTextChanged
import com.sungbin.texteditor.library.R
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
    private var currentLine = 0
    private var lineColor = Color.GRAY
    private var lineNumberColor = Color.GRAY
    private var selectLineColor = Color.CYAN
    private var applyHighlight = true
    private var enableHorizontallyScroll = true
    private lateinit var historyManager: EdittextHistoryManager

    private var reservedColor = Color.argb(255, 21, 101, 192)
    private var numberColor = Color.argb(255, 191, 54, 12)
    private var stringColor = Color.argb(255, 255, 160, 0)
    private var annotationColor = Color.argb(255, 139, 195, 74)

    private var highlighter = CodeHighlighter(
        reservedColor, numberColor,
        stringColor, annotationColor
    )

    /**
     * Create a new TSimpleCodeEditor.
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
        lineColor = attr.getColor(
            R.styleable.SimpleCodeEditor_sce_lineNumberColor,
            this.lineColor
        )
        lineNumberColor = attr.getColor(
            R.styleable.SimpleCodeEditor_sce_lineNumberColor,
            this.lineNumberColor
        )
        selectLineColor = attr.getColor(
            R.styleable.SimpleCodeEditor_sce_selectLineColor,
            this.selectLineColor
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
        background = null
        gravity = Gravity.TOP or Gravity.START
        setHorizontallyScrolling(enableHorizontallyScroll)
        textSize = 14f
        setTextColor(Color.BLACK)
        doAfterTextChanged {
            if (applyHighlight) highlighter.apply(it!!)
        }
        lineNumberPaint.apply {
            textSize = dp * 10.toFloat()
            color = lineNumberColor
            typeface = Typeface.MONOSPACE
        }
        linePaint.color = lineColor
        highlightPaint.apply {
            color = selectLineColor
            alpha = 64
        }
        viewTreeObserver.addOnScrollChangedListener {
            currentLine = lineCount * scrollY / height
            invalidate()
        }
        historyManager = EdittextHistoryManager(this)
    }

    override fun onDraw(canvas: Canvas) {
        val lineCount = lineCount
        val digits = log10(lineCount.toDouble()).toInt()
        val textWidth = lineNumberPaint.measureText(lineCount.toString()).toInt()
        val selectedLine = selectedLine // getter
        getDrawingRect(rect)
        var line = 1
        for (i in 0 until lineCount) {
            if (i != 0 && text?.get(layout.getLineStart(i) - 1) != '\n') continue
            val lineBound = getLineBounds(i, null).toFloat()
            val spaceCount = digits - log10(i.toDouble()).toInt()
            canvas.drawText(
                "${getSpace(spaceCount)}$line",
                rect.left + dp * 4.toFloat(),
                lineBound,
                lineNumberPaint
            )
            line++
        }

        /*for (i in currentLine - 20..currentLine + 80) {
            if (i in 1..lineCount) {
                val baseline = editText!!.getLineBounds(i - 1, lineRect)
                val spaceCount = digits - log10(i.toDouble()).toInt()
                canvas.drawText(
                        getSpace(spaceCount) + i,
                        rect!!.left + dp!! * 4.toFloat(),
                        baseline.toFloat(),
                        lineNumberPaint!!
                )
                if (i - 1 == selectedLine) {
                    lineRect!!.left += rect!!.left - dp!! * 4
                    canvas.drawRect(lineRect!!, highlightPaint!!)
                }
            }
        }*/

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

    fun undo() {
        historyManager.undo()
    }

    fun redo() {
        historyManager.redo()
    }

    fun findText(string: String, ignoreUpper: Boolean = false): ArrayList<ArrayList<Int>> {
        val lines = text.toString().split("\n")
        val array = ArrayList<ArrayList<Int>>()
        for (i in lines.indices) {
            if (ignoreUpper) {
                val text = lines[i].toLowerCase(Locale.KOREA)
                val lowerString = string.toLowerCase(Locale.KOREA)
                if (text.contains(lowerString)) {
                    val index = text.indexOf(lowerString)
                    val list = arrayListOf(i, index)
                    array.add(list)
                }
            } else {
                if (lines[i].contains(string)) {
                    val index = lines[i].indexOf(string)
                    val list = arrayListOf(i, index)
                    array.add(list)
                }
            }
        }
        return array
    }

    private val selectedLine: Int
        get() {
            val selectionStart = Selection.getSelectionStart(text)
            return if (selectionStart != -1) {
                layout.getLineForOffset(selectionStart)
            } else -1
        }

    private fun getSpace(count: Int): String {
        val result = StringBuilder()
        for (i in 0 until count) result.append(" ")
        return result.toString()
    }
}