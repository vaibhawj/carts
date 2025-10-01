package com.example.carts.controller

import com.example.carts.dao.Cart
import com.example.carts.dto.CreateCartRequest
import com.example.carts.repository.CartRepository
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactive.awaitSingleOrNull
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/carts")
class CartController(private val cartRepository: CartRepository) {

    @PostMapping
    suspend fun createCart(@RequestBody request: CreateCartRequest): ResponseEntity<String> {
        val cart = Cart(
            id = UUID.randomUUID().toString(),
            userId = request.userId,
            items = request.items
        )
        val savedCart = cartRepository.save(cart).awaitSingle()
        return ResponseEntity.ok(savedCart.id!!)
    }

    @GetMapping("/{cartId}")
    suspend fun getCart(@PathVariable cartId: String): ResponseEntity<Cart> {
        val cart = cartRepository.findById(cartId)
            .awaitSingleOrNull()
        return if (cart == null) {
            ResponseEntity.notFound().build()
        } else {
            ResponseEntity.ok(cart)
        }
    }
}