package com.myTMS.demo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myTMS.demo.dao.Center;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
class DeliveryServiceTest {

    @Autowired
    private DeliveryService deliveryService;
    @Autowired
    private CenterService centerService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getDistance() {
        centerService.findAllCenters();
        Center closestCenter = centerService.findClosestCenter(Double.parseDouble("129.00661914101872"), Double.parseDouble("35.308459613456534"));// near daegu
//        String distance = deliveryService.getDistance(129.00661914101872,35.308459613456534 ,  closestCenter);
//        log.info(distance);
//
//        try {
//            JsonNode jsonNode = objectMapper.readTree(distance);
//            JsonNode leafNode = jsonNode.path("routes").get(0).path("sections").get(0);
//            int duration = leafNode.path("duration").asInt();
//            double distance1 = leafNode.path("distance").asDouble();
//            log.info("distance1 = {}", distance1);
//            log.info("duration = {}" , duration);
//
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
    }
}