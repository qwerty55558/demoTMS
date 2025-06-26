package com.myTMS.demo.controller;

import com.myTMS.demo.config.security.CustomUserDetails;
import com.myTMS.demo.dao.Cart;
import com.myTMS.demo.dao.typeconst.MessageType;
import com.myTMS.demo.dto.CartListDTO;
import com.myTMS.demo.service.ItemService;
import com.myTMS.demo.service.localrep.CartService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Set;

@Controller
@Slf4j
public class CartController {

    private final CartService cartService;
    private final ItemService itemService;
    private final MessageSource messageSource;

    public CartController(CartService cartService,ItemService itemService, MessageSource messageSource) {
        this.cartService = cartService;
        this.itemService = itemService;
        this.messageSource = messageSource;
    }

    /**
     * 장바구니 목록을 리스팅해주는 메서드
     * @param user 세션에서 유저 정보를 얻어옴
     * @param model 얻어온 데이터를 담아줄 모델
     * @param session 오류가 생길 경우에 세션에 에러메시지를 담고 error 페이지로 redirect
     * @return 장바구니 목록을 보여주는 페이지로 이동
     */
    @GetMapping("/cart")
    public String cartListing(@AuthenticationPrincipal CustomUserDetails user, Model model, HttpSession session, HttpServletRequest req) {
        Cart cart = cartService.getCart(user.getUserId());
        if (cart != null) {
            Set<CartListDTO> items = itemService.findItems(cart);
            long totalPrice = 0L;
            for (CartListDTO item : items) {
                totalPrice += (long) item.getPrice() * item.getQuantity();
            }
            model.addAttribute("cart", items);
            model.addAttribute("totalPrice", totalPrice);
            log.info("Total price = {}", totalPrice);
            for (CartListDTO item : items) {
                log.info("Item = {}, Quantity = {}" , item.getName(), item.getQuantity());
            }
            return "valid/cart";
        } else {
            session.setAttribute(MessageType.errorMessage.name(), messageSource.getMessage("Cart.Empty", null, req.getLocale()));
            return "redirect:/error";
        }
    }
}
