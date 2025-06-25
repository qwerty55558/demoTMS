package com.myTMS.demo.dto.customerservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myTMS.demo.config.security.CustomUserDetails;
import com.myTMS.demo.dao.Message;
import com.myTMS.demo.dao.typeconst.MessageType;
import com.myTMS.demo.handler.MyWebSocketHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Getter
@Setter
public class ChatRoom {
    private String roomId;
    private final ObjectMapper objectMapper = new Jackson2ObjectMapperBuilder().build();
    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();

    public void addSession(WebSocketSession session) {
        sessions.add(session);
    }

    public ChatRoom(WebSocketSession[] sessions) {
        for (WebSocketSession session : sessions) {
            addSession(session);
        }
        this.roomId = UUID.randomUUID().toString();
    }

    public void removeSession(WebSocketSession session) {
        sessions.remove(session);
    }

    public void sendMessage(Message message){
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try{
                    CustomUserDetails userDetails = MyWebSocketHandler.getUserDetailByWebSocket(session);
                    if (message.getMessageType().equals(MessageType.csMessage)){
                        message.setMine(userDetails.getUserId().equals(message.getSenderId()));
                    }

                    session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
                }
                catch (Exception e) {
                    log.info("error sending MSG",e);
                }
            }
        }
    }
}
