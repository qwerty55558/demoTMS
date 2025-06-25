package com.myTMS.demo.dto.user;

import com.myTMS.demo.dao.typeconst.UserType;
import com.myTMS.demo.dto.order.OrderItemListDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
public class UserProfileDTO {
    private String email;
    private OrderItemListDTO orderItemDTO;
    private UserType userType;
}
