package com.myTMS.demo.service;

import com.myTMS.demo.dao.Item;
import com.myTMS.demo.dto.ItemListDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ItemServiceTest {

    @Autowired
    ItemService itemService;

    @Test
    @Transactional
    @Order(1)
    void itemTest() {
        itemService.createItem("item1", "description", 100);
        itemService.setCategory("item1", "outerwear");
        Optional<Item> item1 = itemService.findItem("item1");
        if (item1.isPresent()) {
            log.info("item1: {}", item1.get().getName());
        } else {
            log.info("what happened?");
        }
    }

    @Test
    @Transactional
    @Order(2)
    void itemTest2() {
        List<ItemListDTO> items = itemService.findItems();
        if (items != null) {
            for (ItemListDTO item : items) {
                log.info("itemName: {}", item.getName());
                log.info("itemDescription: {}", item.getDescription());
                log.info("itemPrice: {}", item.getPrice());
                log.info("itemCategory: {}", item.getCategoryId());
            }
        }
    }
}