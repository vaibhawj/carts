package com.example.carts.dto

data class CreateCartResponse(
    val cartId: String,
    val message: String = "Cart created successfully"
)