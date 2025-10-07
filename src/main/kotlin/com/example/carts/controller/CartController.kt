package com.example.carts.controller

import com.example.carts.annotation.LogExecution
import com.example.carts.annotation.LogLevel
import com.example.carts.dto.CreateCartRequest
import com.example.carts.dto.CreateCartResponse
import com.example.carts.model.Cart
import com.example.carts.service.CartService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/carts")
class CartController(
    private val cartService: CartService
) {

    @PostMapping
    @LogExecution(level = LogLevel.INFO, message = "Creating cart via REST API")
    suspend fun createCart(@RequestBody request: CreateCartRequest): ResponseEntity<Any> {
        val cartId = cartService.createCart(request)
        val response = CreateCartResponse(cartId = cartId)
        
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{cartId}")
    @LogExecution(level = LogLevel.DEBUG, message = "Retrieving cart via REST API")
    suspend fun getCart(@PathVariable cartId: String): ResponseEntity<Any> {
        val cart = cartService.getCart(cartId)
        return if (cart == null) {
            val errorResponse = com.example.carts.dto.ErrorResponse(
                status = 404,
                error = "Not Found",
                message = "Cart with ID '$cartId' not found"
            )
            ResponseEntity.status(404).body(errorResponse)
        } else {
            ResponseEntity.ok(cart)
        }
    }
}