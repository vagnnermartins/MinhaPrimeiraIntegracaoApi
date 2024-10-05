package com.example.minhaprimeiraapi.model

data class Item(
    val id: String,
    val value: ItemValue
)

data class ItemValue(
    val id: String
)
