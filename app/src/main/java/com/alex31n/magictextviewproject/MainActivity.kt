package com.alex31n.magictextviewproject

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.alex31n.magictextview.MagicTextView

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)




//        val text =findViewById<MagicTextView>(R.id.text)


        val layout = findViewById<LinearLayout>(R.id.container)

        val text = MagicTextView(this)
        text.setText("Magic Text")
        text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 80f)
        text.setTypeface(ResourcesCompat.getFont(this, R.font.playball), Typeface.BOLD)
        layout.addView(text)
        text.setInnerShadows(15f,5f,5f,Color.BLACK)
        text.setOuterShadows(15f,5f,5f,Color.BLACK)
        text.setStroke(2f,Color.RED, Paint.Join.MITER, 5f)
        text.setTextColor(Color.YELLOW)


    }
}