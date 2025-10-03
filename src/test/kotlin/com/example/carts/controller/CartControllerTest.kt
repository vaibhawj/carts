package com.example.carts.controller

import com.example.carts.dao.Cart as DaoCart
import com.example.carts.dao.Item as DaoItem
import com.example.carts.dto.CreateCartRequest
import com.example.carts.dto.ErrorResponse
import com.example.carts.model.Cart
import com.example.carts.model.Item
import com.example.carts.repository.CartRepository
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import reactor.core.publisher.Mono
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CartControllerTest(
    @Autowired private val webTestClient: WebTestClient,
    @Autowired private val cartRepository: CartRepository,
    @Autowired private val objectMapper: ObjectMapper
) {
    companion object {
        @Container
        @JvmStatic
        val mongoDBContainer = MongoDBContainer("mongo:4.4.6")
            .withExposedPorts(27017)
    }

    @BeforeEach
    fun setUp(): Unit = runBlocking {
        cartRepository.deleteAll().awaitSingleOrNull()
    }

    @Test
    fun shouldCreateCartAndReturnCartId(): Unit = runBlocking {
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
            .expectBody()
            .jsonPath("$.cartId").exists()
            .jsonPath("$.message").isEqualTo("Cart created successfully")
    }

    @Test
    fun shouldRetrieveCreatedCart(): Unit = runBlocking {
        val cartId = UUID.randomUUID().toString()
        val cart = DaoCart(
            id = cartId,
            userId = "user123",
            items = listOf(
                DaoItem(productId = "product1", quantity = 2)
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
    fun shouldReturn404ForNonExistentCart(): Unit = runBlocking {
        val nonExistentId = UUID.randomUUID().toString()

        webTestClient.get()
            .uri("/carts/$nonExistentId")
            .exchange()
            .expectStatus().isNotFound
            .expectBody(ErrorResponse::class.java)
            .consumeWith { response ->
                val errorResponse = response.responseBody
                assertNotNull(errorResponse)
                assertEquals(404, errorResponse.status)
                assertEquals("Not Found", errorResponse.error)
                assert(errorResponse.message.contains("Cart with ID '$nonExistentId' not found"))
            }
    }

    @Test
    fun shouldCreateCartWithEmptyItems(): Unit = runBlocking {
        // Test that empty carts are allowed (validation was removed from service)
        val request = CreateCartRequest(
            userId = "test-user",
            items = emptyList()
        )

        webTestClient.post()
            .uri("/carts")
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(request), CreateCartRequest::class.java)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.cartId").exists()
            .jsonPath("$.message").isEqualTo("Cart created successfully")
    }

    @Test
    fun shouldReturn400ErrorWithJsonResponseForEmptyUserId(): Unit = runBlocking {
        // Test that empty userId triggers validation error
        val request = CreateCartRequest(
            userId = "", // Empty userId should trigger IllegalArgumentException
            items = emptyList()
        )

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

}