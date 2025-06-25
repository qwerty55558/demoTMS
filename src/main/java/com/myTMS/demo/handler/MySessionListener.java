package com.myTMS.demo.handler;

import com.myTMS.demo.config.security.CustomUserDetails;
import com.myTMS.demo.dao.typeconst.UserType;
import com.myTMS.demo.interceptor.WebSocketThrottleInterceptor;
import com.myTMS.demo.service.UserService;
import com.myTMS.demo.service.localrep.EmitterService;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class MySessionListener implements HttpSessionListener {

    private final UserService userService;
    private final EmitterService emitterService;

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        CustomUserDetails userDetailBySession = WebSocketThrottleInterceptor.getUserDetailBySession(se.getSession());
        if (userDetailBySession != null) {
            emitterService.removeEmitter(userDetailBySession.getUserId(), userDetailBySession.getUserType().equals(UserType.Employee));
            userService.logoutUser(userDetailBySession.getUserId());
        }
    }
}
