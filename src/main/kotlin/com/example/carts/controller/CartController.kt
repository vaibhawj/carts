package com.example.carts.controller

import com.example.carts.dto.CreateCartRequest
import com.example.carts.dto.CreateCartResponse
import com.example.carts.model.Cart
import com.example.carts.service.CartService
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/carts")
class CartController(private val cartService: CartService) {

    private val logger = LoggerFactory.getLogger(CartController::class.java)

    @PostMapping
    fun createCart(@RequestBody request: CreateCartRequest): Mono<ResponseEntity<CreateCartResponse>> {
        logger.info("Creating cart for userId: {}, items count: {}", request.userId, request.items.size)
        
        return cartService.createCart(request)
            .map { cartId ->
                val response = CreateCartResponse(cartId = cartId)
                logger.info("Cart created successfully with ID: {}", cartId)
                ResponseEntity.ok(response)
            }
    }

    @GetMapping("/{cartId}")
    fun getCart(@PathVariable cartId: String): Mono<ResponseEntity<Any>> {
        logger.info("Retrieving cart with ID: {}", cartId)
        
        return cartService.getCart(cartId)
            .map { cart ->
                logger.info("Cart retrieved successfully with ID: {}, items count: {}", cartId, cart.items.size)
                ResponseEntity.ok(cart) as ResponseEntity<Any>
            }
            .switchIfEmpty(
                Mono.fromCallable {
                    logger.warn("Cart not found with ID: {}", cartId)
                    val errorResponse = com.example.carts.dto.ErrorResponse(
                        status = 404,
                        error = "Not Found",
                        message = "Cart with ID '$cartId' not found"
                    )
                    ResponseEntity.status(404).body(errorResponse) as ResponseEntity<Any>
                }
            )
    }
}