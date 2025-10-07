package com.example.carts.service

import com.example.carts.annotation.LogExecution
import com.example.carts.annotation.LogLevel
import com.example.carts.dao.CartDaoService
import com.example.carts.dto.CreateCartRequest
import com.example.carts.model.Cart
import org.springframework.stereotype.Service

@Service
class CartService(private val cartDaoService: CartDaoService) {

    @LogExecution(level = LogLevel.INFO, message = "Creating new cart")
    suspend fun createCart(request: CreateCartRequest): String {
        // Basic business logic: validate request
        if (request.userId.isBlank()) {
            throw IllegalArgumentException("User ID is required")
        }
        
        val domainCart = Cart(
            id = null,
            userId = request.userId,
            items = request.items
        )
        
        val createdCart = cartDaoService.create(domainCart)
        
        val cartId = createdCart.id ?: run {
            throw IllegalStateException("Failed to create cart")
        }
        
        return cartId
    }

    @LogExecution(level = LogLevel.DEBUG, message = "Retrieving cart")
    suspend fun getCart(cartId: String): Cart? {
        // Basic business logic: validate cartId
        if (cartId.isBlank()) {
            throw IllegalArgumentException("Cart ID is required")
        }
        
        val cart = cartDaoService.getById(cartId)
        
        return cart
    }
}