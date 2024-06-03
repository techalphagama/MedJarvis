package com.alpharays.mymedjarvisfma

import android.content.Context
import android.content.Intent


object MedJarvisRouter {
    fun startMedJarvisFma(context: Context) {
        val intent = Intent(context, MedJarvisActivity::class.java)
    }
}