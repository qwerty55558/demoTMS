package com.myTMS.demo.dao.abstractclass;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;


@MappedSuperclass
@Getter
@Setter
public abstract class createdAtLastCheckedAt {
    private LocalDateTime createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    private LocalDateTime lastCheckedAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
}
