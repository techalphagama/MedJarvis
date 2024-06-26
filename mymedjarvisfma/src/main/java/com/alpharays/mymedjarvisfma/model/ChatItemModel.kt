package com.alpharays.mymedjarvisfma.model

import android.graphics.Bitmap

data class ChatItemModel(
    val message: String?,
    val isBot: Boolean,
    val image: Bitmap?
)
