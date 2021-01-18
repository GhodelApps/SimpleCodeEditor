package com.sungbin.texteditor.library.util

import android.content.SharedPreferences
import android.text.Editable
import android.text.Selection
import android.text.TextWatcher
import android.text.style.UnderlineSpan
import android.widget.TextView
import java.util.*


/**
 * Created by SungBin on 2020-05-12.
 */

class EditTextHistoryManager(private val view: TextView) {
    private var isUndoOrRedo = false
    private val editHistory: EditHistory
    private val changeListener: EditTextChangeListener

    fun disconnect() {
        view.removeTextChangedListener(changeListener)
    }

    fun setMaxHistorySize(maxHistorySize: Int) {
        editHistory.setMaxHistorySize(maxHistorySize)
    }

    fun clearHistory() {
        editHistory.clear()
    }

    val canUndo: Boolean
        get() = editHistory.position > 0

    fun undo() {
        val edit = editHistory.previous ?: return
        val text = view.editableText
        val start = edit.start
        val end = start + if (edit.after != null) edit.after.length else 0
        isUndoOrRedo = true
        text.replace(start, end, edit.before)
        isUndoOrRedo = false
        for (o in text.getSpans(
            0,
            text.length,
            UnderlineSpan::class.java
        )) {
            text.removeSpan(o)
        }
        Selection.setSelection(
            text,
            if (edit.before == null) start else start + edit.before.length
        )
    }

    val canRedo: Boolean
        get() = editHistory.position < editHistory.history.size

    fun redo() {
        val edit = editHistory.next ?: return
        val text = view.editableText
        val start = edit.start
        val end = start + if (edit.before != null) edit.before.length else 0
        isUndoOrRedo = true
        text.replace(start, end, edit.after)
        isUndoOrRedo = false
        for (o in text.getSpans(
            0,
            text.length,
            UnderlineSpan::class.java
        )) {
            text.removeSpan(o)
        }
        Selection.setSelection(
            text,
            if (edit.after == null) start else start + edit.after.length
        )
    }

    fun storePersistentState(editor: SharedPreferences.Editor, prefix: String) {
        editor.putString("$prefix.hash", view.text.toString().hashCode().toString())
        editor.putInt("$prefix.maxSize", editHistory.maxHistorySizeValue)
        editor.putInt("$prefix.position", editHistory.position)
        editor.putInt("$prefix.size", editHistory.history.size)
        for ((value, index) in editHistory.history.withIndex()) {
            val pre = "$prefix.$value"
            editor.putInt("$pre.start", index.start)
            editor.putString("$pre.before", index.before.toString())
            editor.putString("$pre.after", index.after.toString())
        }
    }

    fun restorePersistentState(sp: SharedPreferences, prefix: String): Boolean {
        return try {
            val ok = doRestorePersistentState(sp, prefix)
            if (!ok) editHistory.clear()
            ok
        } catch (ignored: Exception) {
            false
        }
    }

    private fun doRestorePersistentState(
        sp: SharedPreferences,
        prefix: String
    ): Boolean {
        return try {
            val hash = sp.getString("$prefix.hash", null)
                ?: return true
            if (hash.toInt() != view.text.toString().hashCode()) {
                return false
            }
            editHistory.clear()
            editHistory.maxHistorySizeValue = sp.getInt("$prefix.maxSize", -1)
            val count = sp.getInt("$prefix.size", -1)
            if (count == -1) {
                return false
            }
            for (i in 0 until count) {
                val pre = "$prefix.$i"
                val start = sp.getInt("$pre.start", -1)
                val before = sp.getString("$pre.before", null)
                val after = sp.getString("$pre.after", null)
                if (start == -1 || before == null || after == null) {
                    return false
                }
                editHistory.add(EditItem(start, before, after))
            }
            editHistory.position = sp.getInt("$prefix.position", -1)
            editHistory.position != -1
        } catch (ignored: Exception) {
            false
        }
    }

    private inner class EditHistory {
        var position = 0
        var maxHistorySizeValue = -1
        val history = LinkedList<EditItem>()

        fun clear() {
            position = 0
            history.clear()
        }

        fun add(item: EditItem) {
            while (history.size > position) {
                history.removeLast()
            }
            history.add(item)
            position++
            if (maxHistorySizeValue >= 0) {
                trimHistory()
            }
        }

        fun setMaxHistorySize(maxHistorySize: Int) {
            this.maxHistorySizeValue = maxHistorySize
            if (this.maxHistorySizeValue >= 0) {
                trimHistory()
            }
        }

        fun trimHistory() {
            while (history.size > maxHistorySizeValue) {
                history.removeFirst()
                position--
            }
            if (position < 0) {
                position = 0
            }
        }

        val previous: EditItem?
            get() {
                return if (position == 0) null
                else history[--position]
            }

        val next: EditItem?
            get() {
                if (position >= history.size) {
                    return null
                }
                val item = history[position]
                position++
                return item
            }
    }

    private inner class EditItem(
        val start: Int,
        val before: CharSequence?,
        val after: CharSequence?
    )

    private inner class EditTextChangeListener : TextWatcher {
        private var beforeChange: CharSequence? = null
        private var afterChange: CharSequence? = null

        override fun beforeTextChanged(
            s: CharSequence, start: Int, count: Int,
            after: Int
        ) {
            if (isUndoOrRedo) return
            beforeChange = s.subSequence(start, start + count)
        }

        override fun onTextChanged(
            s: CharSequence, start: Int, before: Int,
            count: Int
        ) {
            try {
                if (isUndoOrRedo) return
                afterChange = s.subSequence(start, start + count)
                editHistory.add(EditItem(start, beforeChange, afterChange))
            } catch (ignored: Exception) {
            }
        }

        override fun afterTextChanged(s: Editable) {}
    }

    init {
        editHistory = EditHistory()
        changeListener = EditTextChangeListener()
        view.addTextChangedListener(changeListener)
    }
}