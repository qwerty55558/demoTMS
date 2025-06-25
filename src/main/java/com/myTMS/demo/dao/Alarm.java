package com.myTMS.demo.dao;

import com.myTMS.demo.dao.abstractclass.createdAt;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Alarm extends createdAt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(lombok.AccessLevel.NONE)
    private Long id;
    private String message;
    private String sourceMsg;
    private Long userId;
}
