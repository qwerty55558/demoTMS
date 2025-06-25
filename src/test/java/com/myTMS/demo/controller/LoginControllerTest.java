package com.myTMS.demo.controller;

import com.myTMS.demo.dto.user.UserSignInDTO;
import com.myTMS.demo.dto.user.UserSignUpDTO;
import com.myTMS.demo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
class LoginControllerTest {

    @LocalServerPort
    private Integer port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void handleValidationExceptions() {
        //given
//        UserSignUpDTO userSignUpDTO = new UserSignUpDTO();
//        userSignUpDTO.setEmail("email");
//        userSignUpDTO.setPw("");
        UserSignInDTO userSignInDTO = new UserSignInDTO();
        userSignInDTO.setEmail("email");
        userSignInDTO.setPw("");

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserSignInDTO> entity = new HttpEntity<>(userSignInDTO, httpHeaders);

//        String url = "http://localhost:" + port + "/signin";

        //when
        ResponseEntity<String> exchange = restTemplate.exchange("/signin", HttpMethod.POST, entity, String.class);

        //then
        assertEquals(HttpStatus.OK, exchange.getStatusCode());
    }
}