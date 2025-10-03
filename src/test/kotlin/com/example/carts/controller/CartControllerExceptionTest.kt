package com.example.carts.controller

import com.example.carts.dto.CreateCartRequest
import com.example.carts.dto.ErrorResponse
import com.example.carts.model.Item
import com.example.carts.service.CartService
import io.mockk.coEvery
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CartControllerExceptionTest(
    @Autowired private val webTestClient: WebTestClient,
    @MockBean private val cartService: CartService
) {

    @Test
    fun shouldReturn500ErrorWithJsonResponseWhenServiceThrowsIllegalStateException(): Unit = runBlocking {
        // Given
        val request = CreateCartRequest(
            userId = "test-user",
            items = listOf(Item(productId = "product1", quantity = 2))
        )

        // Mock the service to throw IllegalStateException
        coEvery { cartService.createCart(any()) } throws IllegalStateException("Failed to create cart")

        // When & Then
        webTestClient.post()
            .uri("/carts")
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(request), CreateCartRequest::class.java)
            .exchange()
            .expectStatus().is5xxServerError
            .expectBody(ErrorResponse::class.java)
            .consumeWith { response ->
                val errorResponse = response.responseBody
                assertNotNull(errorResponse)
                assertEquals(500, errorResponse.status)
                assertEquals("Internal Server Error", errorResponse.error)
                assertEquals("Failed to create cart", errorResponse.message)
                assertEquals("/carts", errorResponse.path)
                assertNotNull(errorResponse.timestamp)
            }
    }

    @Test
    fun shouldReturn500ErrorWithJsonResponseWhenGetCartServiceThrowsException(): Unit = runBlocking {
        // Given
        val cartId = "test-cart-id"

        // Mock the service to throw IllegalStateException
        coEvery { cartService.getCart(any()) } throws IllegalStateException("Database query failed")

        // When & Then
        webTestClient.get()
            .uri("/carts/$cartId")
            .exchange()
            .expectStatus().is5xxServerError
            .expectBody(ErrorResponse::class.java)
            .consumeWith { response ->
                val errorResponse = response.responseBody
                assertNotNull(errorResponse)
                assertEquals(500, errorResponse.status)
                assertEquals("Internal Server Error", errorResponse.error)
                assertEquals("Database query failed", errorResponse.message)
                assertEquals("/carts/$cartId", errorResponse.path)
                assertNotNull(errorResponse.timestamp)
            }
    }

    @Test
    fun shouldReturn400ErrorWithJsonResponseWhenServiceThrowsIllegalArgumentException(): Unit = runBlocking {
        // Given
        val request = CreateCartRequest(
            userId = "", // Empty userId
            items = emptyList()
        )

        // Mock the service to throw IllegalArgumentException
        coEvery { cartService.createCart(any()) } throws IllegalArgumentException("User ID is required")

        // When & Then
        webTestClient.post()
            .uri("/carts")
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(request), CreateCartRequest::class.java)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody(ErrorResponse::class.java)
            .consumeWith { response ->
                val errorResponse = response.responseBody
                assertNotNull(errorResponse)
                assertEquals(400, errorResponse.status)
                assertEquals("Bad Request", errorResponse.error)
                assertEquals("User ID is required", errorResponse.message)
                assertEquals("/carts", errorResponse.path)
                assertNotNull(errorResponse.timestamp)
            }
    }

    @Test
    fun shouldReturn500ErrorWithJsonResponseWhenServiceThrowsGenericException(): Unit = runBlocking {
        // Given
        val request = CreateCartRequest(
            userId = "test-user",
            items = listOf(Item(productId = "product1", quantity = 2))
        )

        // Mock the service to throw a generic RuntimeException
        coEvery { cartService.createCart(any()) } throws RuntimeException("Database connection failed")

        // When & Then
        webTestClient.post()
            .uri("/carts")
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(request), CreateCartRequest::class.java)
            .exchange()
            .expectStatus().is5xxServerError
            .expectBody(ErrorResponse::class.java)
            .consumeWith { response ->
                val errorResponse = response.responseBody
                assertNotNull(errorResponse)
                assertEquals(500, errorResponse.status)
                assertEquals("Internal Server Error", errorResponse.error)
                assertEquals("An unexpected error occurred", errorResponse.message)
                assertEquals("/carts", errorResponse.path)
                assertNotNull(errorResponse.timestamp)
            }
    }
}