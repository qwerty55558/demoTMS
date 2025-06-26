package com.myTMS.demo.handler;

import com.myTMS.demo.dao.typeconst.MessageType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * 인증 부분에서 각 에러를 분기별로 처리하는 CustomEntryPoint, 아이디가 DB 상에 존재하는지 추적할 수 없게 아이디나 비밀번호 매칭 여부에 대해 추상화 하였으며
 * 다른 에러들도 각 에러에 맞게 메시징 하여 alert 창을 띄우고 home 으로 return 할 수 있게 만듦.
 */
@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final MessageSource messageSource;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        Map<String, String> data = new HashMap<>();
        switch (authException) {
            case BadCredentialsException badCredentialsException ->
                    data.put(MessageType.errorMessage.name(), messageSource.getMessage("deny.user.badcredentials", null, request.getLocale()));
            case DisabledException disabledException ->
                    data.put(MessageType.errorMessage.name(), messageSource.getMessage("deny.user.disabled", null, request.getLocale()));
            case InternalAuthenticationServiceException internalAuthenticationServiceException ->
                    data.put(MessageType.errorMessage.name(), messageSource.getMessage("deny.user.badcredentials", null, request.getLocale()));
            case null, default ->
                    data.put(MessageType.errorMessage.name(), messageSource.getMessage("deny.user.unauth", null, request.getLocale()));
        }

        request.getSession().setAttribute(MessageType.errorMessage.name(), data.get(MessageType.errorMessage.name()));
        response.sendRedirect("/error");
    }
}
