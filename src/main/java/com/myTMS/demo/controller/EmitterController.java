package com.myTMS.demo.controller;

import com.myTMS.demo.config.security.CustomUserDetails;
import com.myTMS.demo.dao.typeconst.UserType;
import com.myTMS.demo.dao.wrapper.EmitterWrapper;
import com.myTMS.demo.dto.LocalizeAlarmDTO;
import com.myTMS.demo.service.localrep.EmitterService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/sse")
public class EmitterController {
    private final EmitterService emitterService;

    @GetMapping("/employee")
    public SseEmitter employeeEmitter(@AuthenticationPrincipal CustomUserDetails userDetails,
                                      @RequestHeader(value = "Last-Event-ID", required = false) String lastEventId,
                                      HttpServletRequest req) {
        log.info("Last event id (emp) = {}", lastEventId);

        EmitterWrapper emitter = emitterService.getEmitterWrapper(userDetails.getUserId(), true, req.getLocale());

        if (lastEventId != null) {
            try {
                List<LocalizeAlarmDTO> cacheMessages = emitterService.getCacheMessages(Long.parseLong(lastEventId), true);
                emitterService.sendCacheMessage(emitter, cacheMessages);
            } catch (Exception e) {
                log.warn("Invalid Last-Event-ID on emp: {}", lastEventId);
            }
        }

        return emitter.getEmitter();
    }

    @GetMapping("/general")
    public SseEmitter generalEmitter(@AuthenticationPrincipal CustomUserDetails userDetails,
                                     @RequestHeader(value = "Last-Event-ID", required = false) String lastEventId,
                                     HttpServletRequest req) {
        log.info("Last event id (general) = {}", lastEventId);

        EmitterWrapper emitter = emitterService.getEmitterWrapper(userDetails.getUserId(), false, req.getLocale());

        if (lastEventId != null) {
            try {
                List<LocalizeAlarmDTO> cacheMessages = emitterService.getCacheMessages(Long.parseLong(lastEventId), false);
                emitterService.sendCacheMessage(emitter, cacheMessages);
            } catch (Exception e) {
                log.warn("Invalid Last-Event-ID on general: {}", lastEventId);
            }
        }

        return emitter.getEmitter();
    }

    @PostMapping("/disconnect")
    public ResponseEntity<Void> disconnectEmitter(@AuthenticationPrincipal CustomUserDetails userDetails) {
        emitterService.removeEmitter(userDetails.getUserId(), userDetails.getUserType().equals(UserType.Employee));
        return ResponseEntity.ok().build();
    }


}
