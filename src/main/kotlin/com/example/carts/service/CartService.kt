package com.example.carts.service

import com.example.carts.dao.CartDaoService
import com.example.carts.dto.CreateCartRequest
import com.example.carts.model.Cart
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class CartService(private val cartDaoService: CartDaoService) {

    private val logger = LoggerFactory.getLogger(CartService::class.java)

    suspend fun createCart(request: CreateCartRequest): String {
        logger.debug("Starting cart creation for userId: {}", request.userId)
        
        // Basic business logic: validate request
        if (request.userId.isBlank()) {
            logger.error("Cart creation failed: User ID is required")
            throw IllegalArgumentException("User ID is required")
        }
        
        val domainCart = Cart(
            id = null,
            userId = request.userId,
            items = request.items
        )
        
        logger.debug("Saving cart to database for userId: {}", request.userId)
        val createdCart = cartDaoService.create(domainCart)
        
        val cartId = createdCart.id ?: run {
            logger.error("Cart creation failed: No ID returned from database")
            throw IllegalStateException("Failed to create cart")
        }
        
        logger.debug("Cart created successfully with ID: {}", cartId)
        return cartId
    }

    suspend fun getCart(cartId: String): Cart? {
        logger.debug("Starting cart retrieval for cartId: {}", cartId)
        
        // Basic business logic: validate cartId
        if (cartId.isBlank()) {
            logger.error("Cart retrieval failed: Cart ID is required")
            throw IllegalArgumentException("Cart ID is required")
        }
        
        logger.debug("Querying database for cartId: {}", cartId)
        val cart = cartDaoService.getById(cartId)
        
        if (cart == null) {
            logger.debug("Cart not found in database for cartId: {}", cartId)
        } else {
            logger.debug("Cart found in database for cartId: {}, items count: {}", cartId, cart.items.size)
        }
        
        return cart
    }
}