package com.sungbin.texteditor

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        edit.highlighter.addReservedWord("test")
        sw.setOnCheckedChangeListener { _, isChecked ->
            edit.applyHighlight(isChecked)
        }

    }
}
