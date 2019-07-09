package com.nikeorever.hybird.flutteroid

import android.app.Activity
import android.view.View
import androidx.lifecycle.Lifecycle

fun createFlutterView(activity: Activity, lifecycle: Lifecycle, initialRoute: String): View? {
    return try {
        val flutter = Class.forName("io.flutter.facade.Flutter")
        val createView =
            flutter.getMethod("createView", Activity::class.java, Lifecycle::class.java, String::class.java)
        createView.invoke(null, activity, lifecycle, initialRoute) as View
    } catch (ignored: Exception) {
        null
    }
}