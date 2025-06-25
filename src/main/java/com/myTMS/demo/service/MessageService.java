package com.myTMS.demo.service;

import com.myTMS.demo.dao.Message;
import com.myTMS.demo.repository.interfaces.JPAMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageService {

    private final JPAMessageRepository jpaMessageRepository;

    public void saveMessage(Message message){
        jpaMessageRepository.save(message);
    }

}
