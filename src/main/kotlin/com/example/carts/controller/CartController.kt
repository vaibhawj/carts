package com.example.carts.controller

import com.example.carts.dto.CreateCartRequest
import com.example.carts.model.Cart
import com.example.carts.service.CartService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/carts")
class CartController(private val cartService: CartService) {

    @PostMapping
    suspend fun createCart(@RequestBody request: CreateCartRequest): ResponseEntity<String> {
        val cartId = cartService.createCart(request)
        return ResponseEntity.ok(cartId)
    }

    @GetMapping("/{cartId}")
    suspend fun getCart(@PathVariable cartId: String): ResponseEntity<Cart> {
        val cart = cartService.getCart(cartId)
        return if (cart == null) {
            ResponseEntity.notFound().build()
        } else {
            ResponseEntity.ok(cart)
        }
    }
}