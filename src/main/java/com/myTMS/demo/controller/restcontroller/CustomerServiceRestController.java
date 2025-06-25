package com.myTMS.demo.controller.restcontroller;

import com.myTMS.demo.config.security.CustomUserDetails;
import com.myTMS.demo.dao.typeconst.UserType;
import com.myTMS.demo.service.localrep.ChatService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceRestController {

    private final ChatService chatService;

    @GetMapping("/cs/disconnect")
    public ResponseEntity<Void> disconnect(@AuthenticationPrincipal CustomUserDetails userDetails){
        chatService.dropQueue(userDetails.getUserType(), userDetails.getUserId());
        chatService.destroyChatRoom(userDetails.getUserId());
        log.info("User {} disconnected from customer service", userDetails.getUsername());
        return ResponseEntity.ok().build();
    }
}
