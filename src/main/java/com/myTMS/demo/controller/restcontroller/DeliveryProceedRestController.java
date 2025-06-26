package com.myTMS.demo.controller.restcontroller;

import com.myTMS.demo.config.security.CustomUserDetails;
import com.myTMS.demo.constant.RedisConst;
import com.myTMS.demo.dao.typeconst.MessageType;
import com.myTMS.demo.dao.typeconst.UserType;
import com.myTMS.demo.service.AlarmService;
import com.myTMS.demo.service.DeliveryService;
import com.myTMS.demo.service.OrderService;
import com.myTMS.demo.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api")
public class DeliveryProceedRestController {

    private final OrderService orderService;
    private final DeliveryService deliveryService;
    private final RedisService redisService;
    private final AlarmService alarmService;

    @PostMapping("/delivery/proceed")
    public void proceedDelivery(@RequestBody Map<String, Object> param, @AuthenticationPrincipal CustomUserDetails userDetails){
        Number orderIdNumber = (Number) param.get("orderId");
        Long l = orderIdNumber.longValue();

        if (userDetails.getUserType().equals(UserType.Employee) ||
            userDetails.getUserType().equals(UserType.Admin)) {
            l = orderService.proceedDelivery(l);
        }
//        else{
//            l = orderService.proceedDelivery(l, userDetails.getUserId());
//        }
        if (l != -1L) {
            deliveryService.removeCacheEmDelivery(l);
            // return 값은 주문자의 ID
            alarmService.sendAlarm(l, "alarm.delivery.delivered", false, MessageType.alarmMessage);
        }
        redisService.setObjectData(RedisConst.EMERGENCY_ORDER_CACHE_KEY.name(), deliveryService.getEmergencyDelivery());
    }
}
