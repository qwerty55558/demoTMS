package com.myTMS.demo.service;

import com.myTMS.demo.dao.Alarm;
import com.myTMS.demo.dao.typeconst.MessageType;
import com.myTMS.demo.repository.interfaces.JPAAlarmRepository;
import com.myTMS.demo.service.localrep.EmitterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class AlarmService {
    private final JPAAlarmRepository repository;
    private final EmitterService emitterService;
    private final MessageSource messageSource;

    public Page<Alarm> getAlarmsForEmployee(Long userId, int page, int size){
        return repository.findForUserOrGlobal(userId, Pageable.ofSize(size).withPage(page));
    }

    public Page<Alarm> getAlarmsForGeneral(Long userId, int page, int size) {
        return repository.findPageByUserIdOrderByCreatedAtDesc(userId, Pageable.ofSize(size).withPage(page));
    }

    public void removeAlarms(Long userId) {
        repository.deleteAlarmsByUserId(userId);
    }

    public void sendAlarm(Long userId, String msData, boolean isEmployee, MessageType msgType){
        Alarm alarm = new Alarm();
        alarm.setUserId(userId);
        alarm.setMessage(messageSource.getMessage(msData, null, Locale.KOREA));
        alarm.setSourceMsg(msData);

        repository.save(alarm);

        emitterService.sendToUser(userId, msData, isEmployee, msgType);
    }

    public void sendAlarmToAll(String msData, boolean isEmployee, MessageType msgType) {
        Alarm alarm = new Alarm();
        alarm.setUserId(-1L); // -1L is a placeholder for all users
        alarm.setMessage(messageSource.getMessage(msData, null, Locale.KOREA));
        alarm.setSourceMsg(msData);

        repository.save(alarm);

        emitterService.sendToAllUsers(msData, isEmployee, msgType);
    }
}
