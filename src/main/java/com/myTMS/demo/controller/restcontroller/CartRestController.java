package com.myTMS.demo.controller.restcontroller;

import com.myTMS.demo.config.security.CustomUserDetails;
import com.myTMS.demo.dao.Cart;
import com.myTMS.demo.service.localrep.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class CartRestController {
    private final CartService cartService;


    /**
     * 테스팅시에 사용된 API 이며 단순히 카트를 생성해주고 cart 를 리턴해줌
     */
    @GetMapping("/cart/create")
    public ResponseEntity<Cart> createCart(@AuthenticationPrincipal CustomUserDetails user) {
        log.info("userid = {}", user.getUserId());
        Optional<Cart> cart = cartService.createCart(user.getUserId());
        return cart.map(
                        value -> ResponseEntity.ok().body(value))
                .orElseGet(() -> ResponseEntity.badRequest().build()
                );
    }

    /**
     * 아이템을 추가하면 requestData 에서 itemId 를 가져와 cart 에 아이템을 추가하는 방식
     * @param requestData 해당 html 페이지에서 넘어온 아이템 id
     * @param user CustomUserDetails 로 세션에 저장된 context 에서 Principal 을 가져옴
     * @return CartSize 를 반환함
     */

    @PostMapping("/cart/addItem")
    public ResponseEntity<Integer> addItem(@RequestBody Map<String, String> requestData, @AuthenticationPrincipal CustomUserDetails user) {
        Optional<Cart> cart = cartService.createCart(user.getUserId());
        String itemId = requestData.get("itemId");
        if (cart.isPresent()) {
            Cart prevCart = cart.get();
            return cartService.addItem(prevCart, Long.valueOf(itemId))
                    .map(savedCart -> ResponseEntity.ok().body(savedCart.getCartItemMap().size()))
                    .orElseGet(() -> ResponseEntity.badRequest().build());
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 아이템을 삭제하거나 수량을 줄이는 API
     * Fetch 요청을 통해 수량을 조절하는 방식을 설정했기 때문에 실사용 X
     * 추후에 사용될 가능성이 있어 남겨뒀음
     */


    @PostMapping("/cart/minusItem")
    public ResponseEntity<Cart> minusItem(@RequestBody Map<String, String> requestData, @AuthenticationPrincipal CustomUserDetails user) {
        String itemId = requestData.get("itemId");
        return cartService.minusItem(user.getUserId(), Long.valueOf(itemId))
                .map(value -> ResponseEntity.ok().body(value))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    /**
     * 카트 리스팅을 위한 API 장바구니 아이콘을 클릭하면 해당 세션에 저장된 UserId 를 통해 카트를 가져옴
     * @return Cart 를 반환함
     */

    @GetMapping("/cart/list")
    public ResponseEntity<Cart> getCart(@AuthenticationPrincipal CustomUserDetails user) {
        Cart cart = cartService.getCart(user.getUserId());
        if (cart.getCartItemMap().isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(cart);
        }
    }

    /**
     * html 단에서 sendBeacon 을 통해 fetch 요청을 보내게 되는데 해당 페이지를 떠나거나 이동하면 누적됐던 데이터를 실제 Repository 에 반영하는 단께
     * @param cartData 리스팅된 카트 데이터
     */

    @PostMapping("/cart/fetch")
    public HttpStatus fetchCart(@RequestBody List<Map<String, String>> cartData, @AuthenticationPrincipal CustomUserDetails userDetails) {
        cartService.updateCart(cartData, userDetails.getUserId());
        return HttpStatus.ACCEPTED;
    }

    /**
     * html 단에서 휴지통 아이콘을 클릭하면 호출되는 API, 장바구니를 비워준다.
     */

    @GetMapping("/cart/clear")
    public HttpStatus clearCart(@AuthenticationPrincipal CustomUserDetails userDetails) {
        cartService.removeCart(userDetails.getUserId());
        return HttpStatus.ACCEPTED;
    }
}
