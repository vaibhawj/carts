package com.example.carts.dao

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "carts")
data class Cart(
    @Id
    val id: String? = null,
    val userId: String,
    val items: List<Item> = emptyList()
)