package com.myTMS.demo.controller;

import com.myTMS.demo.config.security.CustomUserDetails;
import com.myTMS.demo.constant.RedisConst;
import com.myTMS.demo.dao.Post;
import com.myTMS.demo.dao.typeconst.Keyword;
import com.myTMS.demo.dto.order.OrderItemListDTO;
import com.myTMS.demo.dto.order.OrderSearchParamDTO;
import com.myTMS.demo.service.AlarmService;
import com.myTMS.demo.service.OrderService;
import com.myTMS.demo.service.PostService;
import com.myTMS.demo.service.RedisService;
import com.myTMS.demo.service.localrep.ChatService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/valid/employee")
public class EmployeeController {

    @Value("${alarm.max.page.size}")
    private Integer pageSize;
    @Value("${orders.max.page.size}")
    private Integer orderPageSize;

    private final PostService postService;
    private final RedisService redisService;
    private final AlarmService alarmService;
    private final ChatService chatService;
    private final MessageSource messageSource;
    private final OrderService orderService;

    @GetMapping("/home")
    public String employeeHome(Model model, @AuthenticationPrincipal CustomUserDetails userDetails, HttpServletRequest request) {
        Page<Post> unansweredPosts = postService.getUnansweredPosts(0, 5);

        model.addAttribute("posts", unansweredPosts);
        model.addAttribute("alarms", alarmService.getAlarmsForEmployee(userDetails.getUserId(), 0, pageSize));
        model.addAttribute("chatCounts", messageSource.getMessage("queue.chat", new Object[]{chatService.getQueueSize()}, request.getLocale()));


        return "valid/employee/home";
    }

    @GetMapping("/department")
    public String departmentPage() {
        return "valid/employee/department";
    }

    @GetMapping("/emergency/order")
    public String emergencyOrderPage(Model model) {
        model.addAttribute("em", redisService.getObjectData(RedisConst.EMERGENCY_ORDER_CACHE_KEY.name()));
        return "valid/employee/emergencyorder";
    }

    @GetMapping("/manage/order")
    public String detailPage(@RequestParam("orderId") Long id, Model model) {
        model.addAttribute("orderId", id);
        return "valid/employee/manageorder";
    }

    @GetMapping("/manage/order/list")
    public String orderListPage(@ModelAttribute(value = "dto") OrderSearchParamDTO DTO, Model model) {
        addBaseAttributes(model);
        return "valid/employee/manageorderlist";
    }

    @PostMapping("/manage/order/list")
    public String orderListPageBySearch(@ModelAttribute(value = "dto") OrderSearchParamDTO DTO, BindingResult br, Model model) {
        try {
            Object castedValue = orderService.dtoToParameter(DTO);
            Page<OrderItemListDTO> orderItemList = orderService.getOrderItemList(
                    0, orderPageSize, DTO.getKeyword(), Sort.by(Sort.Direction.ASC, "id"), castedValue
            );
            model.addAttribute("orders", orderItemList);
            model.addAttribute("keywords", Keyword.values());
            return "valid/employee/manageorderlist";

        } catch (Exception e) {
//            log.warn("검색 파라미터 변환 실패", e);
            br.reject("type", "검색 조건이 올바르지 않습니다.");
            addBaseAttributes(model);
            return "valid/employee/manageorderlist";
        }
    }

    private void addBaseAttributes(Model model) {
        model.addAttribute("orders", orderService.getOrderItemList(PageRequest.of(0, orderPageSize)));
        model.addAttribute("keywords", Keyword.values());
    }
}
