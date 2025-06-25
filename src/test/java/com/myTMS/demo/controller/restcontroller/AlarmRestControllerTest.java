package com.myTMS.demo.controller.restcontroller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myTMS.demo.config.security.CustomUserDetails;
import com.myTMS.demo.dao.Alarm;
import com.myTMS.demo.dao.typeconst.MessageType;
import com.myTMS.demo.service.AlarmService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Slf4j
class AlarmRestControllerTest {

    @Autowired
    AlarmService alarmService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void getAlarm() throws Exception {
        MockHttpSession session = (MockHttpSession) mockMvc.perform(MockMvcRequestBuilders.post("/signin")
                        .param("email", "test1@test.com")
                        .param("pw", "testtest1!"))
                .andReturn()
                .getRequest()
                .getSession();

        assert session != null;
        SecurityContext attribute = (SecurityContext) session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
        CustomUserDetails principal = (CustomUserDetails) attribute.getAuthentication().getPrincipal();

        for (int i = 0; i < 100; i++) {
            alarmService.sendAlarmToAll("alarm.order.emergency", true, MessageType.emMessage);
        }
        alarmService.sendAlarm(principal.getUserId(), "alarm.order.emergency", true, MessageType.alarmMessage);

        HashMap<String, Object> map = new HashMap<>(){
            {
                put("page", 3);
            }
        };

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/alarm/employee")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(map))
                        .session(session))
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();

        JsonNode root = objectMapper.readTree(jsonResponse);

        JsonNode contentArray = root.get("content");

        for (JsonNode alarmNode : contentArray) {
            Long id = alarmNode.get("id").asLong();
            String message = alarmNode.get("message").asText();
            log.info("Alarm ID: {}, Message: {}", id, message);
        }


    }
}