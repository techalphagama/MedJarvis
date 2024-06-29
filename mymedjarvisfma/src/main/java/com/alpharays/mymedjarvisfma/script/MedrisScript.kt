package com.alpharays.mymedjarvisfma.script

object MedrisScript {

    private const val MESSAGE_NOT_RELATED_TO_HEALTH_CARE =
        "the given image or message is not related to healthcare or medicine and do not give any details about it and stop generating anything further"

    fun getUserMessageScript(): String {
        return "The above given image or message is written by a dumb user," +
                " please check if the given image or message is related to healthcare or medicine," +
                " if it is, then please describe about it in bullet points and do not use more than 100 words " +
                "else if it is not related to health care or medicine  please say that $MESSAGE_NOT_RELATED_TO_HEALTH_CARE"
    }
}