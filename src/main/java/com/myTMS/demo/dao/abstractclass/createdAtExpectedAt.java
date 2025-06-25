package com.myTMS.demo.dao.abstractclass;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;


@MappedSuperclass
@Getter
@Setter
public abstract class createdAtExpectedAt {
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Temporal(TemporalType.TIMESTAMP)
    private Date expectedAt;

    @PrePersist
    protected void onCreate(){
        long currentTime = System.currentTimeMillis();
        currentTime = (currentTime / 1000) * 1000; // 밀리초 제거
        Date truncatedDate = new Date(currentTime);
        createdAt = truncatedDate;
        if (expectedAt == null) {
            expectedAt = truncatedDate;
        }
    }
}
