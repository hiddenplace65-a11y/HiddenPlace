package com.hiddenplace

import com.google.gson.annotations.SerializedName

data class Message(
    @SerializedName("id")
    val id: String,
    @SerializedName("text")
    val text: String,
    @SerializedName("fileUrl")
    val fileUrl: String? = null,
    @SerializedName("thumbnail")
    val thumbnail: String? = null,
    @SerializedName("mediaType")
    val mediaType: String? = null, // "image" or "video"
    @SerializedName("timestamp")
    val timestamp: Long = System.currentTimeMillis()
)

data class UploadResponse(
    @SerializedName("fileUrl")
    val fileUrl: String,
    @SerializedName("thumbnail")
    val thumbnail: String?
)
