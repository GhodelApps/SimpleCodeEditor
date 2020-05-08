package com.sungbin.texteditor

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableStringBuilder
import com.sungbin.texteditor.library.ui.BaseEditText
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        edit.editor.text = SpannableStringBuilder("TEST")
        edit.lineBackgroundColor = Color.parseColor("#42A5F5")
        edit.invalidate()
    }
}
