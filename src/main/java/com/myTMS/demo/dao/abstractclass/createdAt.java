package com.myTMS.demo.dao.abstractclass;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * MappedSuperclass 로 등록한 추상 클래스들은 공통된 timestamp 를 extend 함으로써
 * 통일된 내용으로 DB에 쉽게 등록할수 있어 추상 클래스로 등록함
 */
@MappedSuperclass
@Getter
public abstract class createdAt {

    @Column(name = "created_at", updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;


    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now().withNano(0);
    }
}
