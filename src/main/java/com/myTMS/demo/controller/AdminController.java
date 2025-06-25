package com.myTMS.demo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myTMS.demo.constant.RedisConst;
import com.myTMS.demo.constant.StatusCode;
import com.myTMS.demo.dao.Department;
import com.myTMS.demo.dto.MailTemplate;
import com.myTMS.demo.dto.chart.ChartDataDTO;
import com.myTMS.demo.dto.user.EmployeeSignUpDTO;
import com.myTMS.demo.dto.user.UserSignUpDTO;
import com.myTMS.demo.service.DepartmentService;
import com.myTMS.demo.service.RedisService;
import com.myTMS.demo.service.UserService;
import com.myTMS.demo.service.norep.MailService;
import com.myTMS.demo.utils.RandomAuthCodeGenerator;
import jakarta.validation.Valid;
import jdk.jfr.MetadataDefinition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Locale;
import java.util.Optional;

@Controller
@RequestMapping("/valid/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final RedisService redisService;
    private final ObjectMapper objectMapper;
    private final UserService userService;
    private final MailService mailService;
    private final DepartmentService departmentService;

    @GetMapping({"home", ""})
    public String adminHome(Model model) throws JsonProcessingException {
        String objectData = redisService.getObjectData(RedisConst.DASHBOARD_CACHE_KEY.name());
        ChartDataDTO chartDataDTO = objectMapper.readValue(objectData, ChartDataDTO.class);
        model.addAttribute("chartData", chartDataDTO);
        return "/valid/admin/home";
    }

    @GetMapping("/department")
    public String departmentPage() {
        return "valid/employee/department";
    }

    @GetMapping("/emergency/order")
    public String emergencyOrderPage(Model model) {
        model.addAttribute("em", redisService.getObjectData(RedisConst.EMERGENCY_ORDER_CACHE_KEY.name()));
        return "valid/admin/emergencyorder";
    }

    @GetMapping("/enroll/employee")
    public String enrollUser(@ModelAttribute("dto") EmployeeSignUpDTO dto) {
        return "valid/admin/enrollemployee";
    }


    @PostMapping("/enroll/employee")
    public String enrollUserSubmit(@ModelAttribute("dto") @Valid EmployeeSignUpDTO dto, BindingResult br, Model model) {
        if (br.hasErrors()) {
            return "valid/admin/enrollemployee"; // 유효성 오류가 있을 경우 다시 폼으로
        }
        try {
            String tempPw = RandomAuthCodeGenerator.generate();

            UserSignUpDTO userSignUpDTO = new UserSignUpDTO();
            userSignUpDTO.setEmail(dto.getEmail());
            userSignUpDTO.setPw(tempPw);

            Optional<Department> dpmt = departmentService.findDepartmentByName("CustomerService");
            dpmt.ifPresent(department -> userService.signUpEmployee(userSignUpDTO, department));

            MailTemplate mailTemplate = mailService.buildEmpTempPwTemplate(dto.getEmail(), Locale.ENGLISH, tempPw);
            mailService.send(mailTemplate);

            redisService.setStringData(dto.getEmail(), RedisConst.USER_TEMP_PASSWORD.get(), StatusCode.OK.get());
        } catch (Exception e) {
            log.info("Enroll Employee Error");
        }
        return "redirect:/home";
    }


}
