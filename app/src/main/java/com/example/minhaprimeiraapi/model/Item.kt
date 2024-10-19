package com.example.minhaprimeiraapi.model

import java.util.Date

data class Item(
    val id: String,
    val value: ItemValue
)

data class ItemValue(
    val id: String,
    val name: String,
    val surname: String,
    val profession: String,
    val imageUrl: String,
    val age: Int,
    val location: ItemLocation?,
    val date: Date?
)

data class ItemLocation(
    val name: String,
    val latitude: Double,
    val longitude: Double
)