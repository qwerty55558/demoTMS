package com.myTMS.demo.dao;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * 유저 개개인에게 하나씩 발급될 Cart 객체이며 장바구니 역할을 한다
 */
@Getter
@Setter
public class Cart {
    private Long userId;
    private Map<Long, CartItem> cartItemMap = new HashMap<>();
    private Long totalPrice;

    public boolean addItem(Long itemId, CartItem cartItem) throws Exception{
        if (!cartItemMap.containsKey(itemId)) {
            cartItemMap.put(itemId, cartItem);
            return true;
        }
        return false;
    }

    public boolean removeItem(Long itemId) throws Exception{
        if (cartItemMap.containsKey(itemId)) {
            cartItemMap.remove(itemId);
            return true;
        }
        return false;
    }

    public boolean updateItem(CartItem cartItem) throws Exception{
        if (cartItemMap.containsKey(cartItem.getItemId())) {
            cartItemMap.replace(cartItem.getItemId(), cartItem);
            return true;
        }
        return false;
    }

    public boolean plusItem(Long itemId) throws Exception{
        try {
            if (cartItemMap.containsKey(itemId)) {
                CartItem cartItem = cartItemMap.get(itemId);
                cartItem.setQuantity(cartItem.getQuantity() + 1);
            } else {
                CartItem cartItem = new CartItem(itemId,1);
                addItem(itemId, cartItem);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean minusItem(Long itemId) throws Exception{
        if (cartItemMap.containsKey(itemId)) {
            CartItem cartItem = cartItemMap.get(itemId);
            if (cartItem.getQuantity() > 1) {
                cartItem.setQuantity(cartItem.getQuantity() - 1);
            } else {
                removeItem(itemId);
            }
            return true;
        }
        return false;
    }

    public void clearCart() throws Exception{
        cartItemMap.clear();
    }

}
