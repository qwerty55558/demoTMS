package com.myTMS.demo.service;

import com.myTMS.demo.dao.typeconst.DeliveryStatus;
import com.myTMS.demo.dto.order.OrderItemListDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Test
    void testLogics(){
    }
}