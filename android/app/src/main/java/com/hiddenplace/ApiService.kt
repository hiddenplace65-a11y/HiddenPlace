package com.hiddenplace

import okhttp3.MultipartBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @Multipart
    @POST("upload")
    suspend fun uploadMedia(@Part media: MultipartBody.Part): UploadResponse

    @GET("messages")
    suspend fun getMessages(): List<Message>

    @POST("messages")
    suspend fun postMessage(message: Message): Message

    companion object {
        fun create(baseUrl: String): ApiService {
            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
    }
}
