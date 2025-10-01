package com.example.carts.dto

import com.example.carts.model.Item

data class CreateCartRequest(
    val userId: String,
    val items: List<Item> = emptyList()
)