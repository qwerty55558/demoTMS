package com.myTMS.demo.controller.restcontroller;

import com.myTMS.demo.config.security.CustomUserDetails;
import com.myTMS.demo.dto.order.OrderDetailsDTO;
import com.myTMS.demo.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
public class OrderRestController {

    private final OrderService orderService;

    /**
     * 주문 상세 페이지를 구현 parameter 로 orderId 를 받음 (html data 속성으로) 받아온 id 가 실제 유저가 주문한 내역인지 확인 후
     * DTO 객체를 반환받아 이를 전달함
     */
    @GetMapping("/order/detail")
    public ResponseEntity<OrderDetailsDTO> orderDetail(@RequestParam("orderId") Long orderId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Optional<OrderDetailsDTO> orderDetails = orderService.getOrderDetails(orderId, userDetails.getUserId());
        return orderDetails.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/employee/order/detail")
    public ResponseEntity<OrderDetailsDTO> orderDetailForEmployee(@RequestParam("orderId") Long orderId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Optional<OrderDetailsDTO> orderDetails = orderService.getOrderDetails(orderId, userDetails.getUserType());
        return orderDetails.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
