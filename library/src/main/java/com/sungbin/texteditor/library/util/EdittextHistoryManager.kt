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

class EdittextHistoryManager(private val view: TextView) {
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
        get() = editHistory.mmPosition > 0

    fun undo() {
        val edit: EditItem = editHistory.previous ?: return
        val text = view.editableText
        val start = edit.mmStart
        val end = start + if (edit.mmAfter != null) edit.mmAfter.length else 0
        isUndoOrRedo = true
        text.replace(start, end, edit.mmBefore)
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
            if (edit.mmBefore == null) start else start + edit.mmBefore.length
        )
    }

    val canRedo: Boolean
        get() = editHistory.mmPosition < editHistory.mmHistory.size

    fun redo() {
        val edit: EditItem = editHistory.next ?: return
        val text = view.editableText
        val start = edit.mmStart
        val end = start + if (edit.mmBefore != null) edit.mmBefore.length else 0
        isUndoOrRedo = true
        text.replace(start, end, edit.mmAfter)
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
            if (edit.mmAfter == null) start else start + edit.mmAfter.length
        )
    }

    fun storePersistentState(editor: SharedPreferences.Editor, prefix: String) {
        editor.putString("$prefix.hash", view.text.toString().hashCode().toString())
        editor.putInt("$prefix.maxSize", editHistory.mmMaxHistorySize)
        editor.putInt("$prefix.position", editHistory.mmPosition)
        editor.putInt("$prefix.size", editHistory.mmHistory.size)
        for ((i, ei) in editHistory.mmHistory.withIndex()) {
            val pre = "$prefix.$i"
            editor.putInt("$pre.start", ei.mmStart)
            editor.putString("$pre.before", ei.mmBefore.toString())
            editor.putString("$pre.after", ei.mmAfter.toString())
        }
    }

    fun restorePersistentState(sp: SharedPreferences, prefix: String): Boolean {
        return try {
            val ok = doRestorePersistentState(sp, prefix)
            if (!ok) {
                editHistory.clear()
            }
            ok
        }
        catch (ignored: Exception) {
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
            if (Integer.valueOf(hash) != view.text.toString().hashCode()) {
                return false
            }
            editHistory.clear()
            editHistory.mmMaxHistorySize = sp.getInt("$prefix.maxSize", -1)
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
            editHistory.mmPosition = sp.getInt("$prefix.position", -1)
            editHistory.mmPosition != -1
        }
        catch (ignored: Exception) {
            false
        }
    }

    private inner class EditHistory {
        var mmPosition = 0
        var mmMaxHistorySize = -1
        val mmHistory = LinkedList<EditItem>()

        fun clear() {
            mmPosition = 0
            mmHistory.clear()
        }

        fun add(item: EditItem) {
            while (mmHistory.size > mmPosition) {
                mmHistory.removeLast()
            }
            mmHistory.add(item)
            mmPosition++
            if (mmMaxHistorySize >= 0) {
                trimHistory()
            }
        }

        fun setMaxHistorySize(maxHistorySize: Int) {
            mmMaxHistorySize = maxHistorySize
            if (mmMaxHistorySize >= 0) {
                trimHistory()
            }
        }

        fun trimHistory() {
            while (mmHistory.size > mmMaxHistorySize) {
                mmHistory.removeFirst()
                mmPosition--
            }
            if (mmPosition < 0) {
                mmPosition = 0
            }
        }

        val previous: EditItem?
            get() {
                if (mmPosition == 0) {
                    return null
                }
                mmPosition--
                return mmHistory[mmPosition]
            }

        val next: EditItem?
            get() {
                if (mmPosition >= mmHistory.size) {
                    return null
                }
                val item = mmHistory[mmPosition]
                mmPosition++
                return item
            }
    }

    private inner class EditItem(
        val mmStart: Int,
        val mmBefore: CharSequence?,
        val mmAfter: CharSequence?
    )

    private inner class EditTextChangeListener : TextWatcher {
        private var mBeforeChange: CharSequence? = null
        private var mAfterChange: CharSequence? = null
        override fun beforeTextChanged(
            s: CharSequence, start: Int, count: Int,
            after: Int
        ) {
            if (isUndoOrRedo) {
                return
            }
            mBeforeChange = s.subSequence(start, start + count)
        }

        override fun onTextChanged(
            s: CharSequence, start: Int, before: Int,
            count: Int
        ) {
            if (isUndoOrRedo) {
                return
            }
            mAfterChange = s.subSequence(start, start + count)
            editHistory.add(EditItem(start, mBeforeChange, mAfterChange))
        }

        override fun afterTextChanged(s: Editable) {}
    }

    init {
        editHistory = EditHistory()
        changeListener = EditTextChangeListener()
        view.addTextChangedListener(changeListener)
    }
}