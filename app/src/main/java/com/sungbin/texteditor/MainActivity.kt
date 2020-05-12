package com.sungbin.texteditor

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        edit.editor.text = SpannableStringBuilder(
                "1234567890\n" +
                "1234567890\n" +
                "1234567890\n" +
                "1234567890\n" +
                "1234567890\n" +
                "1234567890\n" +
                "1234567890\n" +
                "1234TesT0\n" +
                "1234567890\n" +
                "1234567890"
        )
        Log.d("AAA - 9", edit.findText("TEST", true)[0][1].toString())
    }
}
