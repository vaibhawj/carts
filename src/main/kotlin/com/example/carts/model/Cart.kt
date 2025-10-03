package com.example.carts.model

import com.example.carts.model.Item

data class Cart(
    val id: String? = null,
    val userId: String,
    val items: List<Item>
)