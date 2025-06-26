package com.myTMS.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myTMS.demo.config.security.CustomUserDetails;
import com.myTMS.demo.constant.RedisConst;
import com.myTMS.demo.dao.Alarm;
import com.myTMS.demo.dao.Center;
import com.myTMS.demo.dao.Orders;
import com.myTMS.demo.dao.typeconst.DeliveryStatus;
import com.myTMS.demo.dao.typeconst.MessageType;
import com.myTMS.demo.dao.typeconst.UserType;
import com.myTMS.demo.dto.delivery.EmergencyDeliveryDTO;
import com.myTMS.demo.dto.order.OrderDetailsItemListDTO;
import com.myTMS.demo.dto.order.OrderItemListDTO;
import com.myTMS.demo.repository.interfaces.JPAAlarmRepository;
import com.myTMS.demo.service.AlarmService;
import com.myTMS.demo.service.DeliveryService;
import com.myTMS.demo.service.OrderService;
import com.myTMS.demo.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDate;
import java.util.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
@AutoConfigureMockMvc
class OrderControllerTest {
    @Autowired
    private OrderService orderService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private DeliveryService deliveryService;
    @Autowired
    private AlarmService alarmService;
    @Autowired
    private JPAAlarmRepository jpaAlarmRepository;
    @Autowired
    private RedisService redisService;

    @Test
    void orderSystemCheck() throws Exception {
        MockHttpSession session = (MockHttpSession) mockMvc.perform(MockMvcRequestBuilders.post("/signin")
                        .param("email", "test@test.com")
                        .param("pw", "testtest1!"))
                .andReturn()
                .getRequest()
                .getSession();

        assert session != null;
        mockMvc.perform(MockMvcRequestBuilders.get("/api/cart/create")
                .locale(Locale.KOREA)
                .session(session));


        Map<String, Object> itemData = new HashMap<>();
        itemData.put("itemId", 1);

        String json = objectMapper.writeValueAsString(itemData);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/cart/addItem")
                .content(json)
                .contentType("application/json")
                .session(session));

        mockMvc.perform(MockMvcRequestBuilders.post("/order")
                .param("email", "test0@test.com")
                .param("firstName", "qwer")
                .param("lastName", "zxcv")
                .param("address", "test")
                .param("address2", "test2")
                .param("xAddress", "129.00661914101872")
                .param("yAddress", "35.308459613456534")
                .param("payment.CVV", "123")
                .param("payment.expireDate", "12/25")
                .param("payment.number", "1234123412341234")
                .param("payment.type", "CreditCard")
                .session(session)
        );

        SecurityContext attribute = (SecurityContext) session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
        Assertions.assertNotNull(attribute);
        CustomUserDetails principal = (CustomUserDetails) attribute.getAuthentication().getPrincipal();
        log.info("attribute = {}", principal.getUserId());

        Optional<List<Orders>> orderByUserId = orderService.getOrderByUserId(principal.getUserId());
        orderByUserId.ifPresent(orders -> {
            for (Orders order : orders) {
                log.info("orders = {}",order.getTotalPrice());
//                log.info("expireTime = {}", order.getExpireTime());
                orderService.getUserByOrderId(order.getId()).ifPresent(user -> {
                    log.info("user = {}", user.getEmail());
                });
                Objects.requireNonNull(orderService.getOrderItemByOrderId(order.getId()).orElse(null)).forEach(item -> {
                    log.info("item = {}", item.getItem().getName());
                });
                orderService.getOrderDetails(order.getId(), principal.getUserId()).ifPresent(orderDetailsDTO -> {
                    log.info("orderDetailsDTO = {}", orderDetailsDTO.getOrderId());
                    log.info("userName = {}", orderDetailsDTO.getUserName());
                    log.info("totalPrice = {}", orderDetailsDTO.getTotalPrice());
                    log.info("orderDate = {}", orderDetailsDTO.getOrderDate());
                    log.info("expireDate = {}", orderDetailsDTO.getExceptedDate());
                    log.info("destination = {}", orderDetailsDTO.getDestination());
                    log.info("departure = {}", orderDetailsDTO.getDeparture());
                    log.info("deliveryType = {}", orderDetailsDTO.getDeliveryType());
                    log.info("deliveryStatus = {}", orderDetailsDTO.getDeliveryStatus());
                    List<OrderDetailsItemListDTO> orderItems = orderDetailsDTO.getOrderItems();
                    for (OrderDetailsItemListDTO orderItem : orderItems) {
                        log.info("name = {}", orderItem.getName());
                        log.info("description = {}", orderItem.getDescription());
                        log.info("imgSrc = {}", orderItem.getImgSrc());
                        log.info("amount = {}", orderItem.getAmount());
                        log.info("price = {}", orderItem.getPrice());
                    }
                });
            }

        });
    }

    @Test
    void orderPFSSystemCheck() throws Exception{
        MockHttpSession session = (MockHttpSession) mockMvc.perform(MockMvcRequestBuilders.post("/signin")
                        .param("email", "test3@test.com")
                        .param("pw", "testtest1!"))
                .andReturn()
                .getRequest()
                .getSession();

        assert session != null;

        mockMvc.perform(MockMvcRequestBuilders.post("/profile/edit")
                .param("userType", UserType.Delivery.name())
                .session(session));

        SecurityContext attribute = (SecurityContext) session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
        Assertions.assertNotNull(attribute);
        CustomUserDetails principal = (CustomUserDetails) attribute.getAuthentication().getPrincipal();

        MultiValueMap<String, String> params = getStringStringMultiValueMap(principal);

        mockMvc.perform(MockMvcRequestBuilders.post("/order/overseas")
                .params(params)
                .session(session));



        orderService.getOrderByUserId(principal.getUserId()).ifPresent(orders -> {
            for (Orders order : orders) {
                orderService.proceedDelivery(order.getId(),principal.getUserId());
                orderService.proceedDelivery(order.getId(),principal.getUserId());
                orderService.proceedDelivery(order.getId(),principal.getUserId());

                proceedPostReq(order, session);

                log.info("orders = {}",order.getTotalPrice());
//                log.info("expireTime = {}", order.getExpireTime());
                orderService.getUserByOrderId(order.getId()).ifPresent(user -> {
                    log.info("user = {}", user.getEmail());
                });
                Objects.requireNonNull(orderService.getOrderItemByOrderId(order.getId()).orElse(null)).forEach(item -> {
                    log.info("item = {}", item.getItem().getName());
                });
                orderService.getOrderDetails(order.getId(), principal.getUserId()).ifPresent(orderDetailsDTO -> {
                    log.info("orderDetailsDTO = {}", orderDetailsDTO.getOrderId());
                    log.info("userName = {}", orderDetailsDTO.getUserName());
                    log.info("totalPrice = {}", orderDetailsDTO.getTotalPrice());
                    log.info("orderDate = {}", orderDetailsDTO.getOrderDate());
                    log.info("expireDate = {}", orderDetailsDTO.getExceptedDate());
                    log.info("destination = {}", orderDetailsDTO.getDestination());
                    log.info("departure = {}", orderDetailsDTO.getDeparture());
                    log.info("deliveryType = {}", orderDetailsDTO.getDeliveryType());
                    log.info("deliveryStatus = {}", orderDetailsDTO.getDeliveryStatus());
                    List<OrderDetailsItemListDTO> orderItems = orderDetailsDTO.getOrderItems();
                    for (OrderDetailsItemListDTO orderItem : orderItems) {
                        log.info("name = {}", orderItem.getName());
                        log.info("description = {}", orderItem.getDescription());
                        log.info("imgSrc = {}", orderItem.getImgSrc());
                        log.info("amount = {}", orderItem.getAmount());
                        log.info("price = {}", orderItem.getPrice());
                    }
                });
                orderService.updateOrderDeliveryExpectedAt(order.getId());
                checkEmDelivery();
                proceedPostReq(order, session);
            }
            checkEmDelivery();

            Page<OrderItemListDTO> orderItemList = orderService.getOrderItemList(0, 10, null, Sort.by(Sort.Direction.ASC, "id"), null);
            for (OrderItemListDTO orderItemListDTO : orderItemList) {
                log.info("orderItemListDTO = {}",orderItemListDTO.getOrderId());
            }
        });


        List<EmergencyDeliveryDTO> emergencyDelivery = deliveryService.getEmergencyDelivery();
        for (EmergencyDeliveryDTO deliveryDTO : emergencyDelivery) {
            log.info("delivery = {}",deliveryDTO.getOrderId());
            log.info("center = {}",deliveryDTO.getCenterName());
        }

        redisService.setObjectData(RedisConst.EMERGENCY_ORDER_CACHE_KEY.name(), emergencyDelivery);

        String objectData = redisService.getObjectData(RedisConst.EMERGENCY_ORDER_CACHE_KEY.name());
        log.info("redis Data = {}", objectData);

        String countByDeliveryStatus = orderService.getCountByDeliveryStatusNot(DeliveryStatus.Delivered);
        log.info("countByDeliveryStatus:{}", countByDeliveryStatus);

        Map<Center, Long> centerMapByDeliveryCounts =  deliveryService.getCenterMapByDeliveryCounts();
        for (Center center : centerMapByDeliveryCounts.keySet()) {
            log.info("centerName = {}, counts = {}",center.getName(), centerMapByDeliveryCounts.get(center));
        }
        Map<LocalDate, Long> orderCountMapLast5Days = orderService.getOrderCountMapLast5Days();
        for (LocalDate localDate : orderCountMapLast5Days.keySet()) {
            log.info("date = {}, counts = {}",localDate,orderCountMapLast5Days.get(localDate));
        }
    }

    private void proceedPostReq(Orders order, MockHttpSession session) {
        try {
            mockMvc.perform(MockMvcRequestBuilders.post("/api/delivery/proceed")
                    .param("orderId", String.valueOf(order.getId()))
                    .session(session));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void checkEmDelivery() {
        List<EmergencyDeliveryDTO> emergencyDelivery = deliveryService.getEmergencyDelivery();
        for (EmergencyDeliveryDTO delivery : emergencyDelivery) {
            log.info("em orders = {}", delivery.getDeliveryId());
            if (deliveryService.addCacheEmDelivery(delivery.getDeliveryId())) {
                alarmService.sendAlarmToAll("alarm.order.emergency", true, MessageType.alarmMessage);
            }
        }
        for (Alarm alarm : jpaAlarmRepository.findAll()) {
            log.info("alarm = {}", alarm.getSourceMsg());
        }
    }

    private static MultiValueMap<String, String> getStringStringMultiValueMap(CustomUserDetails principal) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("trackingNumber", "1234123412341234");
        params.add("weightCategory", "HEAVY");

        params.add("DTO.firstName", "qwer");
        params.add("DTO.lastName", "zxcv");
        params.add("DTO.email", principal.getUsername());
        params.add("DTO.address", "test");
        params.add("DTO.address2", "test2");
        params.add("DTO.xAddress", "126.57064014574752");
        params.add("DTO.yAddress", "33.45061052820844");

        params.add("DTO.payment.userId", principal.getUserId().toString());
        params.add("DTO.payment.CVV", "1234");
        params.add("DTO.payment.number", "1234123412341234");
        params.add("DTO.payment.expireDate", "12/25");
        params.add("DTO.payment.type", "CreditCard");
        return params;
    }
// file output logic
//        String contentAsString = mockMvc.perform(MockMvcRequestBuilders.get("/order")
//                        .session(session))
//                .andReturn()
//                .getResponse()
//                .getContentAsString();

//        Files.writeString(Paths.get("test-output/order-page.html"), contentAsString, StandardCharsets.UTF_8);
}