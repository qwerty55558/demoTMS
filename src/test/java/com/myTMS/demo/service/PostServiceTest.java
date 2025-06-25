package com.myTMS.demo.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
class PostServiceTest {

    @Autowired
    PostService postService;

    @Test
    void countTest(){
        String countUnansweredPosts = postService.getCountUnansweredPosts();
        log.info("countUnansweredPosts: {}", countUnansweredPosts);
    }
}