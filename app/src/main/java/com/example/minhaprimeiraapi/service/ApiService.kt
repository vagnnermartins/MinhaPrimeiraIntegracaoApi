package com.example.minhaprimeiraapi.service

import com.example.minhaprimeiraapi.model.Item
import retrofit2.http.GET

interface ApiService {

    @GET("items")
    suspend fun getItems(): List<Item>
}
