package com.example.carts.dao

import com.example.carts.dao.Cart as DaoCart
import com.example.carts.dao.Item as DaoItem
import com.example.carts.model.Cart as DomainCart
import com.example.carts.model.Item as DomainItem
import com.example.carts.repository.CartRepository
import org.springframework.stereotype.Service

@Service
class CartDaoService(private val cartRepository: CartRepository) {

    fun create(domainCart: DomainCart): DomainCart {
        val daoCart = DaoCart(
            id = domainCart.id,
            userId = domainCart.userId,
            items = domainCart.items.map { it ->
                DaoItem(productId = it.productId, quantity = it.quantity)
            }
        )
        val savedDaoCart = cartRepository.save(daoCart)
        return DomainCart(
            id = savedDaoCart.id,
            userId = savedDaoCart.userId,
            items = savedDaoCart.items.map { it ->
                DomainItem(productId = it.productId, quantity = it.quantity)
            }
        )
    }

    fun getById(id: String): DomainCart? {
        val daoCart: DaoCart? = cartRepository.findById(id).orElse(null)
        return daoCart?.let {
            DomainCart(
                id = daoCart.id,
                userId = daoCart.userId,
                items = daoCart.items.map { it ->
                    DomainItem(productId = it.productId, quantity = it.quantity)
                })
        }
    }
}