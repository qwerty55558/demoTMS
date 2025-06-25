package com.myTMS.demo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class RedisService {
    private final RedisTemplate<String, String> redisTemplate;
    private HashOperations<String, String, String> hashOperations;
    private final ObjectMapper objectMapper;

    @Autowired
    public RedisService(RedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    private void init(){
        hashOperations = redisTemplate.opsForHash();
    }

    public void setStringData(String key, String hashKey, String value) {
        hashOperations.put(key, hashKey, value);
    }

    public void deleteDataByString(String key, String hashKey) {
        Long delete = hashOperations.delete(key, hashKey);
        log.info("Delete {} from Redis", delete);
    }

    public void setObjectData(String key, Object value) {
        try {
            String data = objectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, data);
        } catch (JsonProcessingException e) {
            log.info("ObjectMapper Parsing Error",e);
        }
    }

    public void setObjectData(String key, String hashKey , Object value) {
        try {
            String data = objectMapper.writeValueAsString(value);
            hashOperations.put(key, hashKey, data);
        } catch (JsonProcessingException e) {
            log.info("ObjectMapper Parsing Error",e);
        }
    }

    public void setObjectData(String key, String hashKey, List<Object> value) {
        try {
            String data = objectMapper.writeValueAsString(value);
            hashOperations.put(key, hashKey, data);
        } catch (JsonProcessingException e) {
            log.info("ObjectMapper Parsing Error",e);
        }
    }

    public void setObjectData(String key, List<Object> value) {
        try {
            String data = objectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, data);
        } catch (JsonProcessingException e) {
            log.info("ObjectMapper Parsing Error",e);
        }
    }


    public String getObjectData(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public String getData(String key, String hashKey) {
        return hashOperations.get(key, hashKey);
    }

    public List<String> values(String key){
        return hashOperations.values(key);
    }
}
