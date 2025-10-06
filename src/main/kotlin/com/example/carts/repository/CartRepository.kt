package com.example.carts.repository

import com.example.carts.dao.Cart
import org.springframework.data.mongodb.repository.MongoRepository

interface CartRepository : MongoRepository<Cart, String>