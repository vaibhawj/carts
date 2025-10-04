package com.example.carts.dao

import com.example.carts.dao.Cart as DaoCart
import com.example.carts.dao.Item as DaoItem
import com.example.carts.model.Cart as DomainCart
import com.example.carts.model.Item as DomainItem
import com.example.carts.repository.CartRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class CartDaoService(private val cartRepository: CartRepository) {

    fun create(domainCart: DomainCart): Mono<DomainCart> {
        val daoCart = DaoCart(
            id = domainCart.id,
            userId = domainCart.userId,
            items = domainCart.items.map { item ->
                DaoItem(productId = item.productId, quantity = item.quantity)
            }
        )
        return cartRepository.save(daoCart)
            .map { savedDaoCart ->
                DomainCart(
                    id = savedDaoCart.id,
                    userId = savedDaoCart.userId,
                    items = savedDaoCart.items.map { item ->
                        DomainItem(productId = item.productId, quantity = item.quantity)
                    }
                )
            }
    }

    fun getById(id: String): Mono<DomainCart> {
        return cartRepository.findById(id)
            .map { daoCart ->
                DomainCart(
                    id = daoCart.id,
                    userId = daoCart.userId,
                    items = daoCart.items.map { item ->
                        DomainItem(productId = item.productId, quantity = item.quantity)
                    }
                )
            }
    }
}