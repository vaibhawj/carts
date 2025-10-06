package com.example.carts.controller

import com.example.carts.config.SecurityConfig
import com.example.carts.dto.CreateCartRequest
import com.example.carts.dto.ErrorResponse
import com.example.carts.exception.GlobalExceptionHandler
import com.example.carts.model.Item
import com.example.carts.service.CartService
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@WebMvcTest(CartController::class)
@Import(SecurityConfig::class, GlobalExceptionHandler::class)
@ContextConfiguration(classes = [CartController::class, CartControllerExceptionTest.TestConfig::class])
class CartControllerExceptionTest {
    
    @TestConfiguration
    class TestConfig {
        @Bean
        fun cartService(): CartService = mockk()
    }
    
    @Autowired
    private lateinit var mockMvc: MockMvc
    
    @Autowired
    private lateinit var cartService: CartService
    
    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun shouldReturn500ErrorWithJsonResponseWhenServiceThrowsIllegalStateException() {
        // Given
        val request = CreateCartRequest(
            userId = "test-user",
            items = listOf(Item(productId = "product1", quantity = 2))
        )

        // Mock the service to throw IllegalStateException
        every { cartService.createCart(any()) } throws IllegalStateException("Failed to create cart")

        // When & Then
        val result = mockMvc.perform(post("/carts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().is5xxServerError)
            .andReturn()

        val errorResponse = objectMapper.readValue(result.response.contentAsString, ErrorResponse::class.java)
        assertNotNull(errorResponse)
        assertEquals(500, errorResponse.status)
        assertEquals("Internal Server Error", errorResponse.error)
        assertEquals("Failed to create cart", errorResponse.message)
        assertEquals("/carts", errorResponse.path)
        assertNotNull(errorResponse.timestamp)
    }

    @Test
    fun shouldReturn500ErrorWithJsonResponseWhenGetCartServiceThrowsException() {
        // Given
        val cartId = "test-cart-id"

        // Mock the service to throw IllegalStateException
        every { cartService.getCart(any()) } throws IllegalStateException("Database query failed")

        // When & Then
        val result = mockMvc.perform(get("/carts/$cartId"))
            .andExpect(status().is5xxServerError)
            .andReturn()

        val errorResponse = objectMapper.readValue(result.response.contentAsString, ErrorResponse::class.java)
        assertNotNull(errorResponse)
        assertEquals(500, errorResponse.status)
        assertEquals("Internal Server Error", errorResponse.error)
        assertEquals("Database query failed", errorResponse.message)
        assertEquals("/carts/$cartId", errorResponse.path)
        assertNotNull(errorResponse.timestamp)
    }

    @Test
    fun shouldReturn400ErrorWithJsonResponseWhenServiceThrowsIllegalArgumentException() {
        // Given
        val request = CreateCartRequest(
            userId = "", // Empty userId
            items = emptyList()
        )

        // Mock the service to throw IllegalArgumentException
        every { cartService.createCart(any()) } throws IllegalArgumentException("User ID is required")

        // When & Then
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

    @Test
    fun shouldReturn500ErrorWithJsonResponseWhenServiceThrowsGenericException() {
        // Given
        val request = CreateCartRequest(
            userId = "test-user",
            items = listOf(Item(productId = "product1", quantity = 2))
        )

        // Mock the service to throw a generic RuntimeException
        every { cartService.createCart(any()) } throws RuntimeException("Database connection failed")

        // When & Then
        val result = mockMvc.perform(post("/carts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().is5xxServerError)
            .andReturn()

        val errorResponse = objectMapper.readValue(result.response.contentAsString, ErrorResponse::class.java)
        assertNotNull(errorResponse)
        assertEquals(500, errorResponse.status)
        assertEquals("Internal Server Error", errorResponse.error)
        assertEquals("An unexpected error occurred", errorResponse.message)
        assertEquals("/carts", errorResponse.path)
        assertNotNull(errorResponse.timestamp)
    }
}