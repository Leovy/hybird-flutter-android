package com.nikeorever.hybird.flutteroid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.FrameLayout

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val flutterView = createFlutterView(this, lifecycle, "route1")
        flutterView?.apply {
            addContentView(this, FrameLayout.LayoutParams(600, 800).apply {
                gravity = Gravity.CENTER
            })
        }

        Processor().process(this)
        println("MainActivity")
    }
}
