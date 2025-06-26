package com.myTMS.demo.controller.restcontroller;

import com.myTMS.demo.config.security.CustomUserDetails;
import com.myTMS.demo.constant.RedisConst;
import com.myTMS.demo.dao.typeconst.Keyword;
import com.myTMS.demo.dao.typeconst.UserType;
import com.myTMS.demo.dto.order.OrderItemListDTO;
import com.myTMS.demo.dto.order.OrderSearchParamDTO;
import com.myTMS.demo.service.OrderService;
import com.myTMS.demo.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EmployeeRestController {

    private final RedisService redisService;
    private final OrderService orderService;

    @Value("${orders.max.page.size}")
    private Integer ordersPageSize;

    @GetMapping("/employee/department")
    public ResponseEntity<String> getEmployeeDepartment(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails.getUserType().equals(UserType.Employee) ||
                userDetails.getUserType().equals(UserType.Admin)) {
            return ResponseEntity.ok()
                    .body((redisService.getObjectData(RedisConst.DEPARTMENT_CACHE_KEY.name())));
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/employee/order/list")
    public ResponseEntity<Page<OrderItemListDTO>> getOrderListForEmployee(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody Map<String, String> data) {
        if (userDetails.getUserType().equals(UserType.Employee) ||
                userDetails.getUserType().equals(UserType.Admin)) {
            String kw = data.get("keyword");
            String value = data.get("value");
            String page = data.get("page");

            if (kw != null && value != null) {
                Keyword keyword = Keyword.valueOf(kw);
                OrderSearchParamDTO dto = new OrderSearchParamDTO(value, keyword);

                Object param = orderService.dtoToParameter(dto);
                return ResponseEntity.ok().body(orderService.getOrderItemList(Integer.valueOf(page),
                        ordersPageSize, keyword, Sort.by(Sort.Direction.ASC, "id"), param));
            }else {
                return ResponseEntity.ok().body(orderService.getOrderItemList(PageRequest.of(Integer.valueOf(page), ordersPageSize, Sort.by(Sort.Direction.ASC
                        , "id"))));
            }
        }else {
            return ResponseEntity.notFound().build();
        }
    }
}
