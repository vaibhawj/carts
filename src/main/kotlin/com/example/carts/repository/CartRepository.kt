package com.example.carts.repository

import com.example.carts.dao.Cart
import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface CartRepository : ReactiveMongoRepository<Cart, String>