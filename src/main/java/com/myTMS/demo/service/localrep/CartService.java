package com.myTMS.demo.service.localrep;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myTMS.demo.dao.Cart;
import com.myTMS.demo.dao.CartItem;
import com.myTMS.demo.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ObjectMapper objectMapper;

    public Optional<Cart> createCart(Long userId){
        Optional<Cart> userCart = cartRepository.getCart(userId);
        if (userCart.isEmpty()) {
            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setTotalPrice(0L);
            cartRepository.saveCart(cart);
            return Optional.of(cart);
        }else {
            return userCart;
        }
    }

    public Cart getCart(Long userId) {
        Optional<Cart> cart = cartRepository.getCart(userId);
        return cart.orElse(null);
    }

    public Optional<Cart> addItem(Long userId, Long itemId){
        try {
            Cart cart = getCart(userId);
            if (cart.plusItem(itemId)) {
                return cartRepository.saveCart(cart);
            }
            return Optional.empty();
        } catch (Exception e) {
            log.info("addItem Error !",e);
            return Optional.empty();
        }
    }

    public Optional<Cart> addItem(Cart cart, Long itemId) {
        try {
            if (cart.plusItem(itemId)) {
                return cartRepository.saveCart(cart);
            }
            return Optional.empty();
        } catch (Exception e) {
            log.info("addItem Error !",e);
            return Optional.empty();
        }
    }

    public Optional<Cart> minusItem(Long userId, Long itemId){
        try {
            Cart cart = getCart(userId);
            cart.minusItem(itemId);
            return cartRepository.saveCart(cart);
        } catch (Exception e) {
            log.info("minusItem Error !",e);
            return Optional.empty();
        }
    }

    public Optional<Cart> updateItem(Long userId, Long itemId, Integer quantity) {
        try {
            Cart cart = getCart(userId);
            if (quantity == 0) {
                cart.removeItem(itemId);
                return cartRepository.saveCart(cart);
            }
            CartItem cartItem = new CartItem(itemId,quantity);
            if (cart.updateItem(cartItem)) {
                return cartRepository.saveCart(cart);
            }
            return Optional.empty();
        } catch (Exception e) {
            log.info("updateItem Error !",e);
            return Optional.empty();
        }
    }

    public Optional<Cart> clearCart(Long userId) {
        try {
            Cart cart = getCart(userId);
            cart.clearCart();
            return cartRepository.saveCart(cart);
        } catch (Exception e) {
            log.info("clearCart Error !",e);
            return Optional.empty();
        }
    }

    public void removeCart(Long userId) {
        try {
            cartRepository.deleteCart(userId);
        } catch (Exception e) {
            log.info("removeCart Error !",e);
        }
    }

    public void updateCart(List<Map<String, String>> updateData, Long userId) {
        try {
            Cart cart = getCart(userId);
            for (Map<String, String> updateDatum : updateData) {
                String itemId = updateDatum.get("id");
                String quantity = updateDatum.get("quantity");
                if (quantity.equals("0")) {
                    cart.removeItem(Long.valueOf(itemId));
                } else {
                    CartItem cartItem = new CartItem(Long.valueOf(itemId), Integer.valueOf(quantity));
                    cart.updateItem(cartItem);
                }
            }
        } catch (Exception e) {
            log.info("updateCart Error ", e);
        }
    }
}
