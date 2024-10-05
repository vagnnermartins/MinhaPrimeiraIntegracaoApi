package com.example.minhaprimeiraapi.model

data class Item(
    val id: String,
    val value: ItemValue
)

data class ItemValue(
    val id: String,
    val name: String,
    val surname: String,
    val address: String,
    val imageUrl: String,
    val age: Int
)
