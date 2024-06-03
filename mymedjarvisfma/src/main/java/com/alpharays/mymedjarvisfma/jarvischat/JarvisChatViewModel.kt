package com.alpharays.mymedjarvisfma.jarvischat

import android.app.Application
import androidx.lifecycle.ViewModel
import com.alpharays.mymedjarvisfma.jarvischat.di.JarvisChatModule
import com.google.ai.client.generativeai.GenerativeModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class JarvisChatViewModel @Inject constructor(
    @JarvisChatModule.GeminiProVision private val geminiProVision: GenerativeModel,
    @JarvisChatModule.GeminiPro private val geminiPro: GenerativeModel,
    private val application: Application
) : ViewModel() {
}