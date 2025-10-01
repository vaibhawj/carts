package com.example.carts.controller

import com.example.carts.dao.Cart
import com.example.carts.dto.CreateCartRequest
import com.example.carts.model.Item
import com.example.carts.repository.CartRepository
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono
import java.util.UUID

@SpringBootTest(webEnvironment = org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT)
class CartControllerTest(@Autowired private val webTestClient: WebTestClient,
                         @Autowired private val cartRepository: CartRepository,
                         @Autowired private val objectMapper: ObjectMapper) {

    @BeforeEach
    fun setUp() = runBlocking {
        cartRepository.deleteAll().awaitSingle()
    }

    @AfterEach
    fun tearDown() = runBlocking {
        cartRepository.deleteAll().awaitSingle()
    }

    @Test
    fun `should create cart and return cartId`() = runBlocking {
        val request = CreateCartRequest(
            userId = "user123",
            items = listOf(
                Item(productId = "product1", quantity = 2),
                Item(productId = "product2", quantity = 1)
            )
        )

        webTestClient.post()
            .uri("/carts")
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(request), CreateCartRequest::class.java)
            .exchange()
            .expectStatus().isOk
            .expectBody(String::class.java)
            .consumeWith { response ->
                val cartId = response.responseBody
                assert(cartId != null && cartId.isNotEmpty())
            }
    }

    @Test
    fun `should retrieve created cart`() = runBlocking {
        val cartId = UUID.randomUUID().toString()
        val cart = Cart(
            id = cartId,
            userId = "user123",
            items = listOf(
                Item(productId = "product1", quantity = 2)
            )
        )
        cartRepository.save(cart).awaitSingle()

        webTestClient.get()
            .uri("/carts/$cartId")
            .exchange()
            .expectStatus().isOk
            .expectBody(Cart::class.java)
            .consumeWith { response ->
                val retrievedCart = response.responseBody
                assert(retrievedCart?.id == cartId)
                assert(retrievedCart?.userId == "user123")
                assert(retrievedCart?.items?.size == 1)
            }
    }

    @Test
    fun `should return 404 for non-existent cart`() = runBlocking {
        val nonExistentId = UUID.randomUUID().toString()

        webTestClient.get()
            .uri("/carts/$nonExistentId")
            .exchange()
            .expectStatus().isNotFound
    }
}