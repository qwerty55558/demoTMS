package com.myTMS.demo.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myTMS.demo.config.security.CustomUserDetails;
import com.myTMS.demo.dao.Message;
import com.myTMS.demo.dao.typeconst.MessageType;
import com.myTMS.demo.dto.customerservice.ChatRoom;
import com.myTMS.demo.service.MessageService;
import com.myTMS.demo.service.RedisService;
import com.myTMS.demo.service.localrep.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@RequiredArgsConstructor
public class MyWebSocketHandler extends TextWebSocketHandler {

    private final MessageService messageService;
    private final ChatService chatService;
    private final ObjectMapper objectMapper;
    private final RedisService redisService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        CustomUserDetails customUserDetails = getUserDetailByWebSocket(session);
        log.info("WebSocket connection established: {}", customUserDetails.getUsername());
        chatService.enrollQueue(customUserDetails, session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            CustomUserDetails customUserDetails = getUserDetailByWebSocket(session);

            Message payload = objectMapper.readValue(message.getPayload(), Message.class);

            log.info("payload received: {}", payload.getMessage());

            if (payload.getMessage()!=null){
                if (payload.getMessage().length() > 1000) {
                    payload.setMessage(payload.getMessage().substring(0, 1000) + "...");
                }
            }

            if (payload.getMessageType().equals(MessageType.csMessage)){
                payload.setSenderId(customUserDetails.getUserId());

                String roomId = payload.getRoomId();
                ChatRoom chatRoom = chatService.getChatRoom(roomId);
                chatRoom.sendMessage(payload);
                messageService.saveMessage(payload);
            }
            if (payload.getMessageType().equals(MessageType.dropMessage)){
                chatService.dropQueue(customUserDetails.getUserType(), customUserDetails.getUserId());
            }
        }catch (Exception e){
            log.info("WebSocket connection error !!!!!!!!!!!!!!!!", e);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        CustomUserDetails customUserDetails = getUserDetailByWebSocket(session);
        log.info("WebSocket connection closed: {}", customUserDetails.getUsername());
        chatService.destroyChatRoom(customUserDetails.getUserId());
    }

    public static CustomUserDetails getUserDetailByWebSocket(WebSocketSession session) {
        SecurityContext securityContext = (SecurityContext) session.getAttributes().get(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
        return (CustomUserDetails)securityContext
                .getAuthentication().getPrincipal();
    }
}
