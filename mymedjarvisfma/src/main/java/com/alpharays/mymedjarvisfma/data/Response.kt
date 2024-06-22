package com.alpharays.mymedjarvisfma.data

/**
 * @Author: Vinay
 * @Date: 15-01-2024
 */
sealed class Response<out T> {
    object Loading : Response<Nothing>()

    data class Success<out T>(
        val data: T?
    ) : Response<T>()

    data class Failure(
        val message: String
    ) : Response<Nothing>()
}