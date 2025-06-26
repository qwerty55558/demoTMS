package com.myTMS.demo.service;

import com.myTMS.demo.dao.users.Users;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
class UserServiceTest {

    @Autowired
    UserService userService;

    @Autowired
    OrderService orderService;

    @Transactional
    @Test
    void updateUser() {
        Optional<Users> byEmail = userService.findByEmailWithOrders("test@test.com");
        if (byEmail.isPresent()) {
            Users users = byEmail.get();
            log.info("users = {}",users.toString());
//            userService.updateUser(users, UserType.Delivery);
        }
//        Optional<Users> byEmail1 = userService.findByEmail("test@test.com");
//        if (byEmail1.isPresent()) {
//            Users users = byEmail1.get();
//            log.info("users = {}",users.toString());
//            orderService.createOrder(users, 1L);
//        }

        String usersCount = userService.getUsersCount();
        log.info("usersCount = {}",usersCount);

        Map<LocalDate, Long> localDateLongMap = userService.userSignUpTrendLast5Days();
        for (LocalDate localDate : localDateLongMap.keySet()) {
            log.info("localDate = {}, count = {}",localDate,localDateLongMap.get(localDate));
        }
    }
}