package com.alpharays.medjarvis

import android.app.Application
import com.alpharays.mymedjarvisfma.MedJarvisRouter
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MedJarvisApp : Application() {
    //set context
    override fun onCreate() {
        super.onCreate()
    }
}