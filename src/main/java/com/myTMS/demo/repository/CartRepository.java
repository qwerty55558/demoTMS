package com.myTMS.demo.repository;

import com.myTMS.demo.dao.Cart;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class CartRepository {
    private final Map<Long, Cart> cartMap = new ConcurrentHashMap<>();

    public Optional<Cart> getCart(Long userId) {
        return Optional.ofNullable(cartMap.get(userId));
    }

    public Optional<Cart> saveCart(Cart cart) {
        if (cartMap.containsKey(cart.getUserId())) {
            return Optional.ofNullable(cartMap.replace(cart.getUserId(), cart));
        }
        return Optional.ofNullable(cartMap.put(cart.getUserId(), cart));
    }

    public void deleteCart(Long userId) {
        getCart(userId).ifPresent(cart -> cartMap.remove(cart.getUserId()));
    }
}
