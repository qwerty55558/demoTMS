package com.myTMS.demo.service.localrep;

import com.myTMS.demo.dao.repository.ExpiringMap;
import com.myTMS.demo.dao.typeconst.MessageType;
import com.myTMS.demo.dao.wrapper.EmitterWrapper;
import com.myTMS.demo.dto.LocalizeAlarmDTO;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@Service
@Slf4j
public class EmitterService {

    @Value("${alarm.max.cache.size}")
    private int MAX_CACHE_SIZE;

    private final static Object LOCK = new Object();

    private final Map<Long, EmitterWrapper> employeeEmitters = new ConcurrentHashMap<>();
    private final Map<Long, EmitterWrapper> customerEmitters = new ConcurrentHashMap<>();
    private ExpiringMap<Long, LocalizeAlarmDTO> messageCache; // 빈 등록 시에 초기화를 진행하니까 @Value 에 값이 할당되기 전에 초기화하면 안 됨
    private ExpiringMap<Long, LocalizeAlarmDTO> messageCacheEmp;

    private final MessageSource messageSource;

    @PostConstruct
    public void init(){
        messageCache = new ExpiringMap<>(MAX_CACHE_SIZE);
        messageCacheEmp = new ExpiringMap<>(MAX_CACHE_SIZE);
    }

    public EmitterService(MessageSource messageSource){
        this.messageSource = messageSource;
    }


    /**
     * add Cache Message Logics
     */
    public void addCacheMessage(String messageCode, Long millis, boolean isEmployee) {
        ExpiringMap<Long, LocalizeAlarmDTO> targetCache = isEmployee ? messageCacheEmp : messageCache;
        millis = getMillis(millis, targetCache);
        targetCache.put(millis, new LocalizeAlarmDTO(messageCode));
        log.info("cache added [{}] at {}", messageCode, millis);
    }

    public void addCacheParamMessage(String messageCode, Long millis, boolean isEmployee, String... param) {
        ExpiringMap<Long, LocalizeAlarmDTO> targetCache = isEmployee ? messageCacheEmp : messageCache;
        millis = getMillis(millis, targetCache);
        targetCache.put(millis, new LocalizeAlarmDTO(messageCode, param));
        log.info("cache added [{}] at {} with param {}", messageCode, millis, param);
    }

    public void addCacheCountMessage(String messageCode, Long millis, MessageType messageType, String... param){
        LocalizeAlarmDTO localizeAlarmDTO = new LocalizeAlarmDTO(messageCode, param);
        localizeAlarmDTO.setMessageType(messageType);
        millis = getMillis(millis, messageCacheEmp);
        messageCacheEmp.put(millis, localizeAlarmDTO);
        log.info("cache added [{}] at {} with param {} and type {}", messageCode, millis, param, messageType);
    }

    /**
     * get Cache Message Logic
     */
    public List<LocalizeAlarmDTO> getCacheMessages(Long timeStamp, boolean isEmployee) {
        ExpiringMap<Long, LocalizeAlarmDTO> messageCache = isEmployee ? messageCacheEmp : this.messageCache;
        List<Map.Entry<Long, LocalizeAlarmDTO>> entriesAfter = messageCache.getEntriesAfter(timeStamp);
        List<LocalizeAlarmDTO> messages = new ArrayList<>();
        for (Map.Entry<Long, LocalizeAlarmDTO> entry : entriesAfter) {
            messages.add(entry.getValue());
        }
        log.info("messageCache size = {}, returning {} cached entries", messageCache.size(), messages.size());
        return messages;
    }


    /**
     * send Message Logics
     */
    public void sendCacheMessage(EmitterWrapper emitter, List<LocalizeAlarmDTO> messages) {
        for (LocalizeAlarmDTO message : messages) {
            try {
                sendToCacheMessage(emitter, message);
            } catch (Exception e) {
                log.info("Error sending cache Message !!", e);
            }
        }
    }

    public void sendCacheMessage(Long userId, List<LocalizeAlarmDTO> messages, boolean isEmployee) {
        for (LocalizeAlarmDTO message : messages) {
            try {
                sendToCacheMessage(userId, message, isEmployee);
            } catch (Exception e) {
                log.info("Error sending cache Message !!", e);
            }
        }
    }

    public void sendToUser(Long userId, String messageCode, boolean isEmployee, MessageType msgType) {
        Map<Long, EmitterWrapper> targetMap = isEmployee ? employeeEmitters : customerEmitters;
        EmitterWrapper wrapper = targetMap.get(userId);

        Long millis = System.currentTimeMillis();
        addCacheMessage(messageCode, millis, isEmployee);
        if (wrapper != null) {
            try {
                String localizedData = messageSource.getMessage(messageCode, null, wrapper.getLocale());

                wrapper.getEmitter().send(SseEmitter.event()
                        .id(String.valueOf(millis))
                        .name(msgType.name())
                        .data(localizedData));
                log.info("Sent to userId = {}, msg = {}", userId, localizedData);

            } catch (IOException e) {
                targetMap.remove(userId);
                log.warn("Error sending to userId = {}", userId, e);
            }
        }
    }

    public void sendToAllUsers(String messageCode, boolean isEmployee, MessageType msgType) {
        Map<Long, EmitterWrapper> targetMap = isEmployee ? employeeEmitters : customerEmitters;

        Long millis = System.currentTimeMillis();
        addCacheMessage(messageCode, millis, isEmployee);

        targetMap.forEach((userId, wrapper) -> {
            try {
                String localizedData = messageSource.getMessage(messageCode, null, wrapper.getLocale());
                wrapper.getEmitter().send(SseEmitter.event()
                        .id(String.valueOf(millis))
                        .name(msgType.name())
                        .data(localizedData));
                log.info("Broadcasted to userId = {}, msg = {}", userId, localizedData);
            } catch (Exception e) {
                log.warn("Error sending data to userId = {}", userId, e);
                targetMap.remove(userId);
            }
        });
    }

    public void sendToAllUsersCountMsg(String args, boolean isEmployee, MessageType msgType) {
        Map<Long, EmitterWrapper> targetMap = isEmployee ? employeeEmitters : customerEmitters;

        Long millis = System.currentTimeMillis();
        addCacheCountMessage("queue.chat", millis, msgType, args);

        targetMap.forEach((userId, wrapper) -> {
            try {

                String localizedData = messageSource.getMessage("queue.chat", new String[]{args}, wrapper.getLocale());
                wrapper.getEmitter().send(SseEmitter.event()
                        .id(String.valueOf(millis))
                        .name(msgType.name())
                        .data(localizedData));
                log.info("Broadcasted raw args to userId = {}, msg = {}", userId, args);
            } catch (Exception e) {
                log.warn("Error sending raw args to userId = {}", userId, e);
                targetMap.remove(userId);
            }
        });
    }

    public void sendToCacheMessage(Long userId, LocalizeAlarmDTO message, boolean isEmployee) {
        Map<Long, EmitterWrapper> targetMap = isEmployee ? employeeEmitters : customerEmitters;
        EmitterWrapper wrapper = targetMap.get(userId);

        Long millis = System.currentTimeMillis();
        if (wrapper != null) {
            try {
                String localizedData = messageSource.getMessage(message.getMsData(), message.getParams(), wrapper.getLocale());

                if (message.getMessageType() != null && message.getMessageType().equals(MessageType.countMessage)){
                    wrapper.getEmitter().send(SseEmitter.event()
                            .id(String.valueOf(millis))
                            .name(message.getMessageType().name())
                            .data(localizedData));
                }else{
                    wrapper.getEmitter().send(SseEmitter.event()
                            .id(String.valueOf(millis))
                            .name(MessageType.alarmMessage.name())
                            .data(localizedData));

                }
            } catch (IOException e) {
                targetMap.remove(userId);
                log.info("Error sending cache Message !!", e);
            }
        }
    }

    public void sendToCacheMessage(EmitterWrapper emitter, LocalizeAlarmDTO message){
        try{
            String localizedData = messageSource.getMessage(message.getMsData(), message.getParams(), emitter.getLocale());
            Long millis = System.currentTimeMillis();
            if (message.getMessageType() != null && message.getMessageType().equals(MessageType.countMessage)){
                emitter.getEmitter().send(SseEmitter.event()
                        .id(String.valueOf(millis))
                        .name(message.getMessageType().name())
                        .data(localizedData));
            }else{
                emitter.getEmitter().send(SseEmitter.event()
                        .id(String.valueOf(millis))
                        .name("message")
                        .data(localizedData));
            }
        }catch (IOException e){
            log.info("Error sending cache Message !!", e);
        }
    }


    /**
     * CRUD Logics For Emitters
     */

    public SseEmitter getEmitter(Long userId, boolean isEmployee, Locale locale) {
        EmitterWrapper emitterWrapper = getEmitterWrapper(userId, isEmployee, locale);
        return emitterWrapper.getEmitter();
    }

    public EmitterWrapper getEmitterWrapper(Long userId, boolean isEmployee, Locale locale){
        Map<Long, EmitterWrapper> targetMap = isEmployee ? employeeEmitters : customerEmitters;
        EmitterWrapper wrapper = targetMap.get(userId);

        if (wrapper != null) {
            wrapper.getEmitter().complete();
            targetMap.remove(userId);
        }

        wrapper = createEmitter(userId, isEmployee, locale);


        return wrapper;
    }

    public EmitterWrapper createEmitter(Long userId, boolean isEmployee, Locale locale) {
        SseEmitter emitter = new SseEmitter(30 * 1000L);
        EmitterWrapper wrapper = new EmitterWrapper(emitter, locale);

        emitter.onCompletion(() -> removeEmitter(userId, isEmployee));
        emitter.onTimeout(() -> removeEmitter(userId, isEmployee));
        emitter.onError((e) -> removeEmitter(userId, isEmployee));

        if (isEmployee) {
            employeeEmitters.put(userId, wrapper);
        } else {
            customerEmitters.put(userId, wrapper);
        }

        sendInitialConnectMsg(emitter);
        return wrapper;
    }

    public void removeEmitter(Long userId, boolean isEmployee) {
        if (isEmployee) {
            EmitterWrapper emitterWrapper = employeeEmitters.get(userId);
            if (emitterWrapper != null) {
                emitterWrapper.getEmitter().complete();
            }
            employeeEmitters.remove(userId);
        } else {
            EmitterWrapper emitterWrapper = customerEmitters.get(userId);
            if (emitterWrapper != null) {
                emitterWrapper.getEmitter().complete();
            }
            customerEmitters.remove(userId);
        }
    }

    public void sendInitialConnectMsg(SseEmitter emitter){
        try {
            emitter.send(SseEmitter.event()
                    .id(String.valueOf(System.currentTimeMillis()))
                    .name("connect")
                    .data("connected"));
            log.info("Initial connect message sent");
        } catch (IOException e) {
            log.info("Initial connect message error", e);
            emitter.completeWithError(e);
        }
    }

    private static Long getMillis(Long millis, ExpiringMap<Long, LocalizeAlarmDTO> targetCache) {
        synchronized (LOCK){
            while (targetCache.containsKey(millis)){
                millis++;
            }
        }
        return millis;
    }

    // ----------------------------------------------------
}


