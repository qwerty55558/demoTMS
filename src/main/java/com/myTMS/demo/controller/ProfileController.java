package com.myTMS.demo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myTMS.demo.config.security.CustomUserDetails;
import com.myTMS.demo.constant.RedisConst;
import com.myTMS.demo.dao.Orders;
import com.myTMS.demo.dao.typeconst.UserType;
import com.myTMS.demo.dao.users.*;
import com.myTMS.demo.dto.order.OrderItemListDTO;
import com.myTMS.demo.dto.user.UserEditProfileDTO;
import com.myTMS.demo.dto.user.UserProfileDTO;
import com.myTMS.demo.service.OrderService;
import com.myTMS.demo.service.RedisService;
import com.myTMS.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Controller
public class ProfileController {

    private final UserService userService;
    private final RedisService redisService;
    private final ObjectMapper objectMapper;
    private final PasswordEncoder passwordEncoder;
    private final OrderService orderService;
    private final MessageSource messageSource;

    /**
     * 프로필 페이지에 접근하기 위한 메서드
     * 마찬가지로 userDetails 를 통해서 Users 데이터를 받아와 매핑시킨 DTO 를 model 에 추가해준다.
     */
    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        userService.findById(userDetails.getUserId()).ifPresent(users -> {
            UserProfileDTO userProfileDTO = new UserProfileDTO();
            userProfileDTO.setEmail(users.getEmail());
            userProfileDTO.setUserType(users.getUserType());
            String data = redisService.getData(userDetails.getUsername(), RedisConst.USER_ORDER_KEY.get());
            if (data != null) {
                try {
                    objectMapper.readValue(data, new TypeReference<List<OrderItemListDTO>>() {
                    }).stream().findAny().ifPresent(userProfileDTO::setOrderItemDTO);
                } catch (JsonProcessingException e) {
                    log.info("Failed to deserialize order item from Redis", e);
                }
            } else {
                userProfileDTO.setOrderItemDTO(null);
            }
            model.addAttribute("user", userProfileDTO);
        });

        return "valid/profile/profile";
    }

    /**
     * 정보 수정 페이지에서 submit 된 데이터들이 넘어오는 메서드이다.
     * dto 로 매핑되는 데이터를 제외한 타입데이터나 타입속성 데이터는 따로 @RequestParam 으로 받아온다.
     * 만약 사용자가 타입을 변경하기로 했다면 order 가 존재하는 지 확인한 후 존재하면 globalErrorMessage 를 보내고
     * 아니라면 정상적으로 userType 을 변경해준다 (기존 데이터로 새로운 type 의 users 를 생성, 빌더패턴 사용)
     */
    @PostMapping("/profile/edit")
    public String editProfile(@ModelAttribute("user") @Valid UserEditProfileDTO users,
                              BindingResult br,
                              @RequestParam(name = "userTypeSelect", required = false) String changeUserType,
                              @RequestParam(name = "userTypeAttribute", required = false) String changeAttribute,
                              @AuthenticationPrincipal CustomUserDetails userDetails,
                              HttpServletRequest req,
                              Model model) {

        log.info("users = {}", users.toString());
        log.info("changeUserType = {}, changeAttribute = {}", changeUserType, changeAttribute);

        if (StringUtils.hasText(changeUserType)) {
            Optional<List<Orders>> orders = orderService.getOrderByUserId(userDetails.getUserId());

            if (orders.isPresent() && !orders.get().isEmpty()) {
                br.reject("userUpdate.haveOrders", messageSource.getMessage("userUpdate.haveOrders", null, req.getLocale()));
                model.addAttribute("user", users);
                return "valid/profile/edit";
            }

            userService.findById(userDetails.getUserId()).ifPresent(prevUsers ->
                    userService.updateUser(prevUsers, UserType.valueOf(changeUserType), changeAttribute)
            );
            req.getSession().invalidate();
            return "redirect:/home";
        }

        if (br.hasErrors()) {
            model.addAttribute("user", users);
            return "valid/profile/edit";
        }

        userService.findById(userDetails.getUserId()).ifPresent(user -> {
            Users.UsersBuilder<?, ?> builder = user.toBuilder()
                    .password(StringUtils.hasText(users.getPassword()) ? passwordEncoder.encode(users.getPassword()) : user.getPassword())
                    .firstName(StringUtils.hasText(users.getFirstName()) ? users.getFirstName() : user.getFirstName())
                    .lastName(StringUtils.hasText(users.getLastName()) ? users.getLastName() : user.getLastName());

            switch (user.getUserType()) {
                case UserType.Business:
                    ((Business.BusinessUserBuilder) builder).BRN(Objects.nonNull(users.getAttributeValue()) ? users.getAttributeValue() : ((Business) user).getBRN());
                    break;
                case UserType.Delivery:
                    ((PFS.PFSUserBuilder) builder).PCC(Objects.nonNull(users.getAttributeValue()) ? users.getAttributeValue() : ((PFS) user).getPCC());
                    break;
                case UserType.General:
                    // 특성 값 없음
                    break;
            }
            user.updateProfile(builder);

            userService.saveUser(user);
        });


        return "redirect:/profile";
    }

    /**
     * 프로필 수정 페이지를 담당하는 메서드이다.
     * DTO 와 실제 DB 데이터를 매칭하여 매핑해주고
     * 각 유저 타입에 따라 유저타입, 특성값을 다르게 지정해서 model 에 담아준다.
     */
    @GetMapping("/profile/edit")
    public String profileEdit(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        userService.findById(userDetails.getUserId()).ifPresent(users -> {
            UserEditProfileDTO userEditProfileDTO = new UserEditProfileDTO();
            userEditProfileDTO.setEmail(users.getEmail());
            userEditProfileDTO.setFirstName(users.getFirstName());
            userEditProfileDTO.setLastName(users.getLastName());
            userEditProfileDTO.setPassword(null);
            userEditProfileDTO.setUserType(users.getUserType());

            userEditProfileDTO.setCreatedAt(
                    String.valueOf(
                            Duration.between(users.getCreatedAt(),LocalDateTime.now())
                                    .toHours()));

            switch(users.getUserType()) {
                case UserType.Admin:
                    Admin adminType = (Admin) users;
                    userEditProfileDTO.setAttributeName("Admin Code");
                    userEditProfileDTO.setAttributeValue(Optional.ofNullable(adminType.getAdminCode()).orElse(" "));
                    break;
                case UserType.Business:
                    Business businessType = (Business) users;
                    userEditProfileDTO.setAttributeName("Business Registration Number");
                    userEditProfileDTO.setAttributeValue(Optional.ofNullable(businessType.getBRN()).orElse(" "));
                    break;
                case UserType.Delivery:
                    PFS pfsType = (PFS) users;
                    userEditProfileDTO.setAttributeName("Personal Clearance Code");
                    userEditProfileDTO.setAttributeValue(Optional.ofNullable(pfsType.getPCC()).orElse(" "));
                    break;
                case UserType.Employee:
                    Employee employeeType = (Employee) users;
                    userEditProfileDTO.setAttributeName("Department");
                    userEditProfileDTO.setAttributeValue(Optional.ofNullable(employeeType.getDepartment().getName()).orElse(" "));
                    break;
                default:
                    userEditProfileDTO.setAttributeName(null);
                    userEditProfileDTO.setAttributeValue(null);
                    break;
            }
            model.addAttribute("user", userEditProfileDTO);
        });
        return "valid/profile/edit";
    }
}
