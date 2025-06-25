package com.myTMS.demo.dao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.myTMS.demo.dao.abstractclass.createdAt;
import com.myTMS.demo.dao.typeconst.MessageType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@JsonIgnoreProperties({"createdAt"})
public class Message extends createdAt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(lombok.AccessLevel.NONE)
    @JsonIgnore
    private Long id;
    @JsonIgnore
    private Long senderId;
    private String roomId;
    private String message;
    @Transient
    private MessageType messageType;
    @Transient
    private boolean isMine;
}
