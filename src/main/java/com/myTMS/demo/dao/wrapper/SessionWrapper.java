package com.myTMS.demo.dao.wrapper;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.socket.WebSocketSession;

@Getter
@Setter
public class SessionWrapper {
    private Long userId;
    private WebSocketSession session;
}
