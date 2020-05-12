package com.sungbin.texteditor.library.ui

import android.content.Context
import android.graphics.*
import android.text.Editable
import android.text.Selection
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.Gravity
import android.widget.EditText
import android.widget.ScrollView
import com.sungbin.texteditor.library.R
import com.sungbin.texteditor.library.util.CodeHighlighter
import com.sungbin.texteditor.library.util.EdittextHistoryManager
import java.util.*
import kotlin.math.log10

class SimpleCodeEditor : ScrollView {
    private var dp: Int? = null
    private var rect: Rect? = null
    private var lineRect: Rect? = null
    private var lineNumberPaint: Paint? = null
    private var linePaint: Paint? = null
    private var highlightPaint: Paint? = null
    private var currentLine = 0
    private var lineColor = Color.GRAY
    private var lineNumberColor = Color.GRAY
    private var selectLineColor = Color.CYAN
    private var edittext: EditText? = null
    private var reservedColor = Color.argb(255, 21, 101, 192)
    private var numberColor = Color.argb(255, 191, 54, 12)
    private var stringColor = Color.argb(255, 255, 160, 0)
    private var annotationColor = Color.argb(255, 139, 195, 74)

    private var readOnly = false
    var applyHighlight = true
    var highlighter = CodeHighlighter(
        reservedColor, numberColor,
        stringColor, annotationColor
    )

    private var edittextHistoryManager: EdittextHistoryManager? = null

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
        context: Context?,
        attrs: AttributeSet?
    ) : super(context, attrs){
        val a = context!!.obtainStyledAttributes(attrs,
            R.styleable.SimpleCodeEditor,
            0,
            0
        )
        lineColor =  a.getColor(
            R.styleable.SimpleCodeEditor_sce_lineNumberColor,
            this.lineColor
        )
        lineNumberColor = a.getColor(
            R.styleable.SimpleCodeEditor_sce_lineNumberColor,
            this.lineNumberColor
        )
        selectLineColor = a.getColor(
            R.styleable.SimpleCodeEditor_sce_selectLineColor,
            this.selectLineColor
        )
        reservedColor = a.getColor(
            R.styleable.SimpleCodeEditor_sce_reservedColor,
            this.reservedColor
        )
        numberColor = a.getColor(
            R.styleable.SimpleCodeEditor_sce_numberColor,
            this.numberColor
        )
        stringColor = a.getColor(
            R.styleable.SimpleCodeEditor_sce_stringColor,
            this.stringColor
        )
        annotationColor = a.getColor(
            R.styleable.SimpleCodeEditor_sce_annotationColor,
            this.annotationColor
        )
        readOnly = a.getBoolean(
            R.styleable.SimpleCodeEditor_sce_readOnly,
            this.readOnly
        )
        applyHighlight = a.getBoolean(
            R.styleable.SimpleCodeEditor_sce_applyHighlighter,
            this.applyHighlight
        )
        highlighter = CodeHighlighter(
            reservedColor, numberColor,
            stringColor, annotationColor
        )
        a.recycle()

        isFillViewport = true
        dp = context.resources.displayMetrics.density.toInt()
        edittext = EditText(context)
        edittext!!.background = null
        edittext!!.gravity = Gravity.TOP or Gravity.START
        edittext!!.setHorizontallyScrolling(true)
        edittext!!.textSize = 14f
        edittext!!.isEnabled = !readOnly
        edittext!!.setTextColor(Color.BLACK)
        edittext!!.typeface = Typeface.MONOSPACE
        edittext!!.viewTreeObserver.addOnDrawListener {
            invalidate()
        }
        edittext!!.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(edittable: Editable?) {
                if(applyHighlight) highlighter.apply(edittable!!)
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })
        addView(edittext!!)

        rect = Rect()
        lineRect = Rect()
        lineNumberPaint = Paint()
        lineNumberPaint!!.textSize = dp!! * 10.toFloat()
        lineNumberPaint!!.color = lineNumberColor
        lineNumberPaint!!.typeface = Typeface.MONOSPACE
        linePaint = Paint()
        linePaint!!.color = lineColor
        highlightPaint = Paint()
        highlightPaint!!.color = selectLineColor
        highlightPaint!!.alpha = 64
        viewTreeObserver.addOnScrollChangedListener {
            currentLine = edittext!!.lineCount * scrollY / getChildAt(0).height
            invalidate()
        }

        edittextHistoryManager = EdittextHistoryManager(
            edittext!!
        )
    }

    fun undo(){
        edittextHistoryManager!!.undo()
    }

    fun redo(){
        edittextHistoryManager!!.redo()
    }

    fun findText(string: String, ignoreUpper: Boolean = false): ArrayList<ArrayList<Int>> {
        val lines = edittext!!.text.split("\n")
        val array = ArrayList<ArrayList<Int>>()
        for(i in lines.indices){
            if(ignoreUpper){
                val text = lines[i].toLowerCase(Locale.getDefault())
                val lowerString = string.toLowerCase(Locale.getDefault())
                if(text.contains(lowerString)){
                    val index = text.indexOf(lowerString)
                    val list = ArrayList<Int>()
                    list.add(i)
                    list.add(index)
                    array.add(list)
                }
            }
            else {
                if(lines[i].contains(string)){
                    val index = lines[i].indexOf(string)
                    val list = ArrayList<Int>()
                    list.add(i)
                    list.add(index)
                    array.add(list)
                }
            }
        }
        return array
    }

    override fun onDraw(canvas: Canvas) {
        val lineCount = edittext!!.lineCount
        val digits = log10(lineCount.toDouble()).toInt()
        val textWidth = lineNumberPaint!!.measureText("" + lineCount).toInt()
        val selectedLine = selectedLine
        getDrawingRect(rect)

        for (i in currentLine - 20..currentLine + 80) {
            if (i in 1..lineCount) {
                val baseline = edittext!!.getLineBounds(i - 1, lineRect)
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
        }

        canvas.drawLine(
            rect!!.left + textWidth + (dp!! * 8).toFloat(),
            rect!!.top.toFloat(),
            rect!!.left + textWidth + (dp!!  * 8).toFloat(),
            rect!!.bottom.toFloat(),
            linePaint!!
        )

        edittext!!.setPadding(textWidth + dp!! * 12, 0, 0, 0)
        super.onDraw(canvas)
    }

    private val selectedLine: Int
        get() {
            val selectionStart = Selection.getSelectionStart(edittext!!.text)
            val layout = edittext!!.layout
            return if (selectionStart != -1) {
                layout.getLineForOffset(selectionStart)
            } else -1
        }

    private fun getSpace(count: Int): String {
        val result = StringBuilder()
        for (i in 0 until count) result.append(" ")
        return result.toString()
    }

    val editor: EditText
        get() = edittext!!
}