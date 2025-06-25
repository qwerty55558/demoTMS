package com.myTMS.demo.dto;

import java.time.LocalDate;

public interface OrderCountProjection {
    LocalDate getDate();
    Long getCount();
}
