package com.myTMS.demo.controller.restcontroller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myTMS.demo.dto.user.UserSignInDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Locale;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class EmailRestControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void sendEmail() throws Exception {
        UserSignInDTO userSignInDTO = new UserSignInDTO();
        userSignInDTO.setEmail("");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/email/valid")
                        .content(objectMapper.writeValueAsBytes(userSignInDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .locale(Locale.KOREA))
                .andExpect(MockMvcResultMatchers.status().isOk());
        Thread.sleep(200000);

    }
}