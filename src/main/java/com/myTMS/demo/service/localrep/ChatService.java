package com.myTMS.demo.service.localrep;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myTMS.demo.config.security.CustomUserDetails;
import com.myTMS.demo.dao.Message;
import com.myTMS.demo.dao.typeconst.MessageType;
import com.myTMS.demo.dao.typeconst.UserType;
import com.myTMS.demo.dao.wrapper.SessionWrapper;
import com.myTMS.demo.dto.customerservice.ChatRoom;
import com.myTMS.demo.handler.MyWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {

    private final ObjectMapper objectMapper;
    private final EmitterService emitterService;

    private final Map<String, ChatRoom> chatRooms = new ConcurrentHashMap<>();

    private final Map<Long, String> userRoomMap = new ConcurrentHashMap<>();

    private final Queue<SessionWrapper> userQueue = new ConcurrentLinkedQueue<>();
    private final Queue<SessionWrapper> employeeQueue = new ConcurrentLinkedQueue<>();

    public ChatRoom getChatRoom(String roomId){
        return chatRooms.get(roomId);
    }

    public void removeChatRoom(String roomId){
        chatRooms.remove(roomId);
    }

    public ChatRoom createChatRoom(WebSocketSession... sessions) {
        ChatRoom chatRoom = new ChatRoom(sessions);
        chatRooms.put(chatRoom.getRoomId(), chatRoom);
        log.info("Created new chat room with ID: {}", chatRoom.getRoomId());
        return chatRoom;
    }

    public ChatRoom enrollQueue(CustomUserDetails userDetails, WebSocketSession session) {
        SessionWrapper sessionWrapper = new SessionWrapper();
        sessionWrapper.setSession(session);
        sessionWrapper.setUserId(userDetails.getUserId());
        if (userDetails.getUserType().equals(UserType.Employee)) {
            employeeQueue.add(sessionWrapper);
        }else{
            userQueue.add(sessionWrapper);
            emitterService.sendToAllUsersCountMsg(String.valueOf(getQueueSize()), true, MessageType.countMessage);
        }
        log.info("User {} with ID {} enrolled in {} queue", userDetails.getUsername(), userDetails.getUserId(), userDetails.getUserType());
        return matchUser();
    }

    public Integer getQueueSize(){
        return userQueue.size();
    }

    public void dropQueue(UserType userType, Long userId) {
        if (userType.equals(UserType.Employee)) {
            dropUserInQueue(userId, employeeQueue);
        } else {
            dropUserInQueue(userId, userQueue);
            emitterService.sendToAllUsersCountMsg(String.valueOf(getQueueSize()), true, MessageType.countMessage);
        }
        log.info("Removed {} from {} queue", userId, userType);
    }

    private void dropUserInQueue(Long userId, Queue<SessionWrapper> userQueue) {
        userQueue.removeIf(sessionWrapper -> {
            if (sessionWrapper.getUserId().equals(userId)) {
                try {
                    sessionWrapper.getSession().close();
                } catch (Exception e) {
                    log.error("Error closing session for employee {}: {}", userId, e.getMessage());
                }
                return true;
            }
            return false;
        });
    }

    public void destroyChatRoom(Long userId) {
        String roomId = userRoomMap.get(userId);
        if (roomId != null) {
            ChatRoom chatRoom = chatRooms.get(roomId);
            if (chatRoom != null) {
                chatRoom.getSessions().forEach(session -> {
                    try {
                        Message message = new Message();
                        message.setMessageType(MessageType.closeMessage);
                        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));

                        CustomUserDetails userDetailByWebSocket = MyWebSocketHandler.getUserDetailByWebSocket(session);
                        userRoomMap.remove(userDetailByWebSocket.getUserId());

                        session.close();
                    } catch (Exception e) {
                        log.error("Error closing session: {}", e.getMessage());
                    }
                });
                removeChatRoom(roomId);
            }
        }
    }

    private ChatRoom matchUser() {
        if (!userQueue.isEmpty() && !employeeQueue.isEmpty()) {
            WebSocketSession userSession = userQueue.poll().getSession();
            WebSocketSession employeeSession = employeeQueue.poll().getSession();
            ChatRoom chatRoom = createChatRoom(userSession, employeeSession);

            CustomUserDetails userDetails = MyWebSocketHandler.getUserDetailByWebSocket(userSession);
            CustomUserDetails employeeDetails = MyWebSocketHandler.getUserDetailByWebSocket(employeeSession);

            userRoomMap.put(userDetails.getUserId(), chatRoom.getRoomId());
            userRoomMap.put(employeeDetails.getUserId(), chatRoom.getRoomId());
            log.info("Matched User: {} with Employee: {} in room: {}",
                     userDetails.getUsername(), employeeDetails.getUsername(), chatRoom.getRoomId());
            Message msg = new Message();
            msg.setMessageType(MessageType.ackMessage);
            msg.setRoomId(chatRoom.getRoomId());
            chatRoom.sendMessage(msg);

            return chatRoom;
        }
        return null;
    }


}

