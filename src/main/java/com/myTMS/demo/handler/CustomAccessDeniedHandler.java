package com.myTMS.demo.handler;

import com.myTMS.demo.dao.typeconst.MessageType;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

/**
 * Spring Security 에서 사용되는 AccessDeniedHandler 이다. AuthenticationEntryPoint 에서 model 을 받아올 수 없기 때문에
 * 에러 핸들링은 세션에 데이터를 등록했다가 출력하면서 세션 등록 데이터를 삭제하는 방식으로 처리했다.
 * 권한이 없는 유저는 alert 으로 에러 코드를 띄우며 (허가되지 않은 페이지) home 으로 redirect 된다.
 */
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final MessageSource messageSource;

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException)
            throws IOException, ServletException {

        request.getSession().setAttribute(MessageType.errorMessage.name(), messageSource.getMessage("deny.page.notAllowed", null, request.getLocale()));
        response.sendRedirect("/error");
    }
}
