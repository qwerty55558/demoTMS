package com.myTMS.demo.interceptor;

import com.myTMS.demo.config.security.CustomUserDetails;
import com.myTMS.demo.constant.AttributeConst;
import com.myTMS.demo.dao.typeconst.UserType;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Slf4j
public class WebSocketThrottleInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            HttpSession session = servletRequest.getServletRequest().getSession(false);
            if (session != null) {
                CustomUserDetails userDetailBySession = getUserDetailBySession(session);
                if (userDetailBySession.getUserType().equals(UserType.Employee)) {
                    return true;
                }
                Long lastTime = (Long) session.getAttribute(AttributeConst.LAST_REQUEST_TIME.name());
                long now = System.currentTimeMillis();

                if (lastTime != null && (now - lastTime < 20000)) {
                    response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                    log.info("WebSocket connection rejected due to throttling: too many requests in a short time.");
                    return false;
                }
                session.setAttribute(AttributeConst.LAST_REQUEST_TIME.name(), now);
            }
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // No-op
    }

    public static CustomUserDetails getUserDetailBySession(HttpSession httpSession) {
        Object attribute = httpSession.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
        if (attribute instanceof SecurityContext securityContext) {
            Object principal = securityContext.getAuthentication().getPrincipal();
            if (principal instanceof CustomUserDetails customUserDetails) {
                return customUserDetails;
            }
        }
        return null;
    }
}

