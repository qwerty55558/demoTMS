package com.myTMS.demo.service.localrep;

import com.myTMS.demo.dto.LocalizeAlarmDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
class EmitterServiceTest {
    @Autowired
    private EmitterService emitterService;
    
    @Test
    void testCaching(){
        for (int i = 0; i < 1000; i++) {
            emitterService.addCacheMessage("chat.closed", System.currentTimeMillis(), true);
        }
    }
}