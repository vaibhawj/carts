package com.example.carts.service

import com.example.carts.dao.CartDaoService
import com.example.carts.dto.CreateCartRequest
import com.example.carts.model.Cart
import org.springframework.stereotype.Service

@Service
class CartService(private val cartDaoService: CartDaoService) {

    suspend fun createCart(request: CreateCartRequest): String {
        // Basic business logic: validate request
        if (request.items.isEmpty()) {
            throw IllegalArgumentException("Cart must contain at least one item")
        }
        if (request.userId.isBlank()) {
            throw IllegalArgumentException("User ID is required")
        }
        val domainCart = Cart(
            id = null,
            userId = request.userId,
            items = request.items
        )
        val createdCart = cartDaoService.create(domainCart)
        return createdCart.id ?: throw IllegalStateException("Failed to create cart")
    }

    suspend fun getCart(cartId: String): Cart? {
        // Basic business logic: validate cartId
        if (cartId.isBlank()) {
            throw IllegalArgumentException("Cart ID is required")
        }
        return cartDaoService.getById(cartId)
    }
}