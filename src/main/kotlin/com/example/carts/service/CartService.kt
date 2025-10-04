package com.example.carts.service

import com.example.carts.dao.CartDaoService
import com.example.carts.dto.CreateCartRequest
import com.example.carts.model.Cart
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class CartService(private val cartDaoService: CartDaoService) {

    private val logger = LoggerFactory.getLogger(CartService::class.java)

    fun createCart(request: CreateCartRequest): Mono<String> {
        logger.debug("Starting cart creation for userId: {}", request.userId)
        
        // Basic business logic: validate request
        if (request.userId.isBlank()) {
            logger.error("Cart creation failed: User ID is required")
            return Mono.error(IllegalArgumentException("User ID is required"))
        }
        
        val domainCart = Cart(
            id = null,
            userId = request.userId,
            items = request.items
        )
        
        logger.debug("Saving cart to database for userId: {}", request.userId)
        return cartDaoService.create(domainCart)
            .map { createdCart ->
                val cartId = createdCart.id ?: run {
                    logger.error("Cart creation failed: No ID returned from database")
                    throw IllegalStateException("Failed to create cart")
                }
                logger.debug("Cart created successfully with ID: {}", cartId)
                cartId
            }
    }

    fun getCart(cartId: String): Mono<Cart> {
        logger.debug("Starting cart retrieval for cartId: {}", cartId)
        
        // Basic business logic: validate cartId
        if (cartId.isBlank()) {
            logger.error("Cart retrieval failed: Cart ID is required")
            return Mono.error(IllegalArgumentException("Cart ID is required"))
        }
        
        logger.debug("Querying database for cartId: {}", cartId)
        return cartDaoService.getById(cartId)
            .doOnNext { cart ->
                logger.debug("Cart found in database for cartId: {}, items count: {}", cartId, cart.items.size)
            }
            .switchIfEmpty(
                Mono.defer {
                    logger.debug("Cart not found in database for cartId: {}", cartId)
                    Mono.empty()
                }
            )
    }
}