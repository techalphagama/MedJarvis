package com.alpharays.mymedjarvisfma.jarvischat.di

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.generationConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@[Module InstallIn(SingletonComponent::class)]
object JarvisChatModule {
    private val harassment = SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.ONLY_HIGH)
    private val hateSpeech =
        SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.MEDIUM_AND_ABOVE)

    private val config = generationConfig {
        temperature = 0.99f
        topK = 50
        topP = 0.99f
    }

    @[Provides Singleton GeminiPro]
    fun provideGemini(): GenerativeModel {
        return GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = API_KEY,
            safetySettings = listOf(
                harassment, hateSpeech
            ),
            generationConfig = config
        )
    }

    @[Provides Singleton GeminiProVision]
    fun provideGeminiVision(): GenerativeModel {
        return GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = API_KEY,
            safetySettings = listOf(
                harassment,
                hateSpeech,
                SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.ONLY_HIGH),
                SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.MEDIUM_AND_ABOVE)
            ),
        )
    }

    private const val API_KEY = "AIzaSyBWzKmCWhAGr9JC5xNA-hYIRfZnEhh5ZmU"


    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class GeminiPro

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class GeminiProVision
}

