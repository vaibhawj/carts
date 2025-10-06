package com.example.carts.controller

import com.example.carts.dao.Cart as DaoCart
import com.example.carts.dao.Item as DaoItem
import com.example.carts.dto.CreateCartRequest
import com.example.carts.dto.ErrorResponse
import com.example.carts.model.Cart
import com.example.carts.model.Item
import com.example.carts.repository.CartRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class CartControllerTest(
    @Autowired private val mockMvc: MockMvc,
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
    fun setUp() {
        cartRepository.deleteAll()
    }

    @Test
    fun shouldCreateCartAndReturnCartId() {
        val request = CreateCartRequest(
            userId = "user123",
            items = listOf(
                Item(productId = "product1", quantity = 2),
                Item(productId = "product2", quantity = 1)
            )
        )

        mockMvc.perform(post("/carts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.cartId").exists())
            .andExpect(jsonPath("$.message").value("Cart created successfully"))
    }

    @Test
    fun shouldRetrieveCreatedCart() {
        val cartId = UUID.randomUUID().toString()
        val cart = DaoCart(
            id = cartId,
            userId = "user123",
            items = listOf(
                DaoItem(productId = "product1", quantity = 2)
            )
        )
        cartRepository.save(cart)

        val result = mockMvc.perform(get("/carts/$cartId"))
            .andExpect(status().isOk)
            .andReturn()

        val retrievedCart = objectMapper.readValue(result.response.contentAsString, Cart::class.java)
        assertEquals(cartId, retrievedCart.id)
        assertEquals("user123", retrievedCart.userId)
        assertEquals(1, retrievedCart.items.size)
    }

    @Test
    fun shouldReturn404ForNonExistentCart() {
        val nonExistentId = UUID.randomUUID().toString()

        val result = mockMvc.perform(get("/carts/$nonExistentId"))
            .andExpect(status().isNotFound)
            .andReturn()

        val errorResponse = objectMapper.readValue(result.response.contentAsString, ErrorResponse::class.java)
        assertNotNull(errorResponse)
        assertEquals(404, errorResponse.status)
        assertEquals("Not Found", errorResponse.error)
        assert(errorResponse.message.contains("Cart with ID '$nonExistentId' not found"))
    }

    @Test
    fun shouldCreateCartWithEmptyItems() {
        // Test that empty carts are allowed (validation was removed from service)
        val request = CreateCartRequest(
            userId = "test-user",
            items = emptyList()
        )

        mockMvc.perform(post("/carts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.cartId").exists())
            .andExpect(jsonPath("$.message").value("Cart created successfully"))
    }

    @Test
    fun shouldReturn400ErrorWithJsonResponseForEmptyUserId() {
        // Test that empty userId triggers validation error
        val request = CreateCartRequest(
            userId = "", // Empty userId should trigger IllegalArgumentException
            items = emptyList()
        )

        val result = mockMvc.perform(post("/carts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest)
            .andReturn()

        val errorResponse = objectMapper.readValue(result.response.contentAsString, ErrorResponse::class.java)
        assertNotNull(errorResponse)
        assertEquals(400, errorResponse.status)
        assertEquals("Bad Request", errorResponse.error)
        assertEquals("User ID is required", errorResponse.message)
        assertEquals("/carts", errorResponse.path)
        assertNotNull(errorResponse.timestamp)
    }

}