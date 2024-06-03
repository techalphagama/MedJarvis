package com.alpharays.mymedjarvisfma

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity


object MedJarvisRouter {
    fun startMedJarvisFma(context: Context) {
        val intent = Intent(context, MedJarvisActivity::class.java)
        startActivity(context, intent, null)
    }
}