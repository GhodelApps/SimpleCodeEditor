package com.sungbin.texteditor.library.util

import android.text.Editable
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import java.util.*

@Suppress("NAME_SHADOWING")
class CodeHighlighter(
    private var reservedColor: Int,
    private var numberColor: Int,
    private var stringColor: Int,
    private var annotationColor: Int
) {

    private val data = ArrayList<Highlighter>()

    private data class Highlighter(var value: String, var color: Int)

    init {
        initHighlightData()
    }

    private fun initHighlightData() {
        val reservedColorData = arrayOf(
            "function",
            "return",
            "var",
            "let",
            "const",
            "if",
            "else",
            "switch",
            "for",
            "while",
            "do",
            "break",
            "continue",
            "case",
            "in",
            "with",
            "true",
            "false",
            "new",
            "null",
            "undefined",
            "typeof",
            "delete",
            "try",
            "catch",
            "finally",
            "prototype",
            "this",
            "super",
            "default",
            "java",
            "io",
            "Jsoup"
        )
        for (n in reservedColorData.indices) {
            data.add(
                Highlighter(
                    reservedColorData[n],
                    -1
                )
            )
        }
    }

    fun addReservedWord(word: String?, color: Int) {
        data.add(Highlighter(word!!, color))
    }

    fun addReservedWord(word: String?) {
        data.add(Highlighter(word!!, -1))
    }

    fun removeReservedWord(word: String) {
        var index = -1
        for (n in data.indices) {
            if (data[n].value == word) {
                index = n
            }
        }
        if (index >= 0) data.removeAt(index)
    }

    fun clearReservedWord() {
        for (n in data.indices) {
            data.removeAt(n)
        }
    }

    fun setReservedWordHighlightColor(color: Int) {
        reservedColor = color
    }

    fun setNumberHighlightColor(color: Int) {
        numberColor = color
    }

    fun setStringHighlightColor(color: Int) {
        stringColor = color
    }

    fun setAnnotationHighlightColor(color: Int) {
        annotationColor = color
    }

    fun apply(editable: Editable) {
        Thread {
            try {
                val string = editable.toString()
                if (string.isEmpty()) return@Thread
                val spans =
                    editable.getSpans(0, editable.length, ForegroundColorSpan::class.java)
                for (n in spans.indices) {
                    editable.removeSpan(spans[n])
                }
                var start = 0
                while (start >= 0) {
                    val index = string.indexOf("/*", start)
                    var end = string.indexOf("*/", index + 2)
                    if (index >= 0 && end >= 0) {
                        editable.setSpan(
                            ForegroundColorSpan(annotationColor),
                            index, end + 2,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    } else {
                        end = -5
                    }
                    start = end + 2
                }
                start = 0
                while (start >= 0) {
                    val index = string.indexOf("//", start)
                    var end = string.indexOf("\n", index + 1)
                    if (index >= 0 && end >= 0) {
                        editable.setSpan(
                            ForegroundColorSpan(annotationColor),
                            index, end,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    } else {
                        end = -1
                    }
                    start = end
                }
                start = 0
                while (start >= 0) {
                    var index = string.indexOf("\"", start)
                    while (index > 0 && string[index - 1] == '\\') {
                        index = string.indexOf("\"", index + 1)
                    }
                    var end = string.indexOf("\"", index + 1)
                    while (end > 0 && string[end - 1] == '\\') {
                        end = string.indexOf("\"", end + 1)
                    }
                    if (index >= 0 && end >= 0) {
                        var span = editable.getSpans(
                            index,
                            end + 1,
                            ForegroundColorSpan::class.java
                        )
                        if (span.isNotEmpty()) {
                            if (string.substring(index + 1, end).contains("/*") && string.substring(
                                    index + 1,
                                    end
                                ).contains("*/")
                            ) {
                                for (n in span.indices) {
                                    editable.removeSpan(span[n])
                                }
                                editable.setSpan(
                                    ForegroundColorSpan(stringColor),
                                    index, end + 1,
                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                                )
                            } else if (string.substring(index + 1, end).contains("//")) {
                                span = editable.getSpans(
                                    index,
                                    string.indexOf("\n", end),
                                    ForegroundColorSpan::class.java
                                )
                                for (n in span.indices) {
                                    editable.removeSpan(span[n])
                                }
                                editable.setSpan(
                                    ForegroundColorSpan(stringColor),
                                    index, end + 1,
                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                                )
                            }
                        } else {
                            editable.setSpan(
                                ForegroundColorSpan(stringColor),
                                index, end + 1,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                            )
                        }
                    } else {
                        end = -5
                    }
                    start = end + 1
                }
                start = 0
                while (start >= 0) {
                    var index = string.indexOf("'", start)
                    while (index > 0 && string[index - 1] == '\\') {
                        index = string.indexOf("'", index + 1)
                    }
                    var end = string.indexOf("'", index + 1)
                    while (end > 0 && string[end - 1] == '\\') {
                        end = string.indexOf("'", end + 1)
                    }
                    if (index >= 0 && end >= 0) {
                        var span = editable.getSpans(
                            index,
                            end + 1,
                            ForegroundColorSpan::class.java
                        )
                        if (span.isNotEmpty()) {
                            if (string.substring(index + 1, end).contains("/*") && string.substring(
                                    index + 1,
                                    end
                                ).contains("*/")
                            ) {
                                for (n in span.indices) {
                                    editable.removeSpan(span[n])
                                }
                                editable.setSpan(
                                    ForegroundColorSpan(stringColor),
                                    index, end + 1,
                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                                )
                            } else if (string.substring(index + 1, end).contains("//")) {
                                span = editable.getSpans(
                                    index,
                                    string.indexOf("\n", end),
                                    ForegroundColorSpan::class.java
                                )
                                for (n in span.indices) {
                                    editable.removeSpan(span[n])
                                }
                                editable.setSpan(
                                    ForegroundColorSpan(stringColor),
                                    index, end + 1,
                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                                )
                            }
                        } else {
                            editable.setSpan(
                                ForegroundColorSpan(stringColor),
                                index, end + 1,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                            )
                        }
                    } else {
                        end = -5
                    }
                    start = end + 1
                }
                for (n in data.indices) {
                    start = 0
                    while (start >= 0) {
                        val index = string.indexOf(data[n].value, start)
                        var end = index + data[n].value.length
                        if (index >= 0) {
                            var color = data[n].color
                            if (color == -1) color = reservedColor
                            if (editable.getSpans(
                                    index,
                                    end,
                                    ForegroundColorSpan::class.java
                                ).isEmpty() && isSeparated(string, index, end - 1)
                            ) editable.setSpan(
                                ForegroundColorSpan(color),
                                index, end,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                            )
                        } else {
                            end = -1
                        }
                        start = end
                    }
                }
                val numberColorData =
                    arrayOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", ".")
                for (n in numberColorData.indices) {
                    start = 0
                    while (start >= 0) {
                        val index = string.indexOf(numberColorData[n], start)
                        var end = index + 1
                        if (index >= 0) {
                            if (editable.getSpans(
                                    index,
                                    end,
                                    ForegroundColorSpan::class.java
                                ).isEmpty() && checkNumber(string, index)
                            ) editable.setSpan(
                                ForegroundColorSpan(numberColor),
                                index, end,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                            )
                        } else {
                            end = -1
                        }
                        start = end
                    }
                }
            } catch (ignored: Exception) {
            }
        }.start()
    }

    private fun checkNumber(str: String, index: Int): Boolean {
        return try {
            val start = getStartPos(str, index)
            val end = getEndPos(str, index)
            if (str[end - 1] == '.') return false
            return if (start == 0) {
                if (str[start] == '.') false else isNumber(str.substring(start, end))
            } else {
                if (str[start + 1] == '.') false else isNumber(str.substring(start + 1, end))
            }
        } catch (ignored: Exception) {
            false
        }
    }

    private fun isSplitPoint(char: Char): Boolean {
        return if (char == '\n') true else " []{}()+-*/%&|!?:;,<>=^~".contains(char.toString())
    }

    private fun getStartPos(str: String, index: Int): Int {
        var index = index
        while (index >= 0) {
            if (isSplitPoint(str[index])) return index
            index--
        }
        return 0
    }

    private fun getEndPos(str: String, index: Int): Int {
        var index = index
        while (str.length > index) {
            if (isSplitPoint(str[index])) return index
            index++
        }
        return str.length
    }

    private fun isSeparated(str: String, start: Int, end: Int): Boolean {
        return try {
            var front = false
            val points = " []{}()+-*/%&|!?:;,<>=^~.".toCharArray()
            if (start == 0) {
                front = true
            } else if (str[start - 1] == '\n') {
                front = true
            } else {
                for (n in points.indices) {
                    if (str[start - 1] == points[n]) {
                        front = true
                        break
                    }
                }
            }
            if (front) {
                try {
                    if (str[end + 1] == '\n') {
                        return true
                    } else {
                        for (n in points.indices) {
                            if (str[end + 1] == points[n]) return true
                        }
                    }
                } catch (ignored: Exception) {
                    return true
                }
            }
            false
        } catch (ignored: Exception) {
            false
        }
    }

    private fun isNumber(value: String): Boolean {
        return try {
            val a = java.lang.Double.valueOf(value)
            true
        } catch (e: Exception) {
            false
        }
    }
}