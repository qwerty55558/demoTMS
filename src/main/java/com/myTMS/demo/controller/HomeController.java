package com.myTMS.demo.controller;

import com.myTMS.demo.config.security.CustomUserDetails;
import com.myTMS.demo.dao.typeconst.UserType;
import com.myTMS.demo.dto.user.UserFindPwDTO;
import com.myTMS.demo.dto.user.UserSignInDTO;
import com.myTMS.demo.dto.user.UserSignUpDTO;
import com.myTMS.demo.service.CategoryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
@RequiredArgsConstructor
public class HomeController {

    /**
     * 비회원 전용 home 리다이렉트 엔드포인트
     * 세션이 존재할 경우에는 valid/home 으로 이동
     * update : UserPrincipal 을 사용해 세션을 확인, UserType에 따라서 이동되는 페이지가 다름
     */
    @GetMapping({"/", "/home"})
    public String home(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (userDetails != null) {
            if (userDetails.getUserType().equals(UserType.Employee)) {
                return "redirect:/valid/employee/home";
            }
            if (userDetails.getUserType().equals(UserType.Admin)) {
                return "redirect:/valid/admin/home";
            }
            return "redirect:/valid/home";
        }
        homeEmptyObjectInclude(model);
        return "home";
    }

    /**
     * thymeleaf 를 통한 form 작성을 위해
     * model 을 파라미터로 받아서 비어있는 객체를 model 에 등록해줌
     */
    public static void homeEmptyObjectInclude(Model model) {
        UserFindPwDTO userFindPwDTO = new UserFindPwDTO();
        UserSignInDTO userSignInDTO = new UserSignInDTO();
        UserSignUpDTO userSignUpDTO = new UserSignUpDTO();

        model.addAttribute("signin", userSignInDTO);
        model.addAttribute("signup", userSignUpDTO);
        model.addAttribute("findpw", userFindPwDTO);
    }
}
