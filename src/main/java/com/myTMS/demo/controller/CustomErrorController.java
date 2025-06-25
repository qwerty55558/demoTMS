package com.myTMS.demo.controller;

import com.myTMS.demo.dao.typeconst.MessageType;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class CustomErrorController implements ErrorController {

    private final MessageSource messageSource;

    /**
     * 분기별로 에러를 처리할 수 있는 Controller 구성
     * 엔드포인트 /error 의 경우에는 AuthenticationEntryPoint 의 에러 분기에 따라서 메시지가 달라지기 때문에
     * 세션에 데이터를 담아 에러메시지를 처리함
     * update: 404 NOT_FOUND 등의 에러 발생시에 HttpStatus CODE 를 통해 에러메시지를 출력함
     *         톰캣에서는 에러 페이지를 처리할 때 미리 정의된 에러페이지를 사용하기 때문에 application.properties 에서 whitelabel 을 false 로 설정하고
     *         커스텀 에러처리를 핸들러로 지정해서 사용함
     * @return 각 페이지에 따라 에러 처리를 다르게 구성
     */
    @GetMapping("/error")
    public String errorPage(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        String errorMessage;

        if (status != null && Integer.parseInt(status.toString()) == HttpStatus.NOT_FOUND.value()) {
            errorMessage = messageSource.getMessage("Page.NotFound", null, request.getLocale());
        } else if (session != null && session.getAttribute(MessageType.errorMessage.name()) != null) {
            errorMessage = session.getAttribute(MessageType.errorMessage.name()).toString();
            session.removeAttribute(MessageType.errorMessage.name());
        } else {
            errorMessage = messageSource.getMessage("Error.Default", null, request.getLocale());
        }

        model.addAttribute(MessageType.errorMessage.name(), errorMessage);
        return "error/defaultError";
    }

}
