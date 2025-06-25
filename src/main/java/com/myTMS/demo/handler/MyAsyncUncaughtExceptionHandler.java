package com.myTMS.demo.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import java.lang.reflect.Method;

/**
 * 동기 처리 시에 생기는 에러를 핸들링 할 수 있는 핸들러 로그만 띄우도록 했다
 */
@Slf4j
public class MyAsyncUncaughtExceptionHandler implements AsyncUncaughtExceptionHandler {
    @Override
    public void handleUncaughtException(Throwable throwable, Method method, Object... params) {
        log.info("AsyncUncaughtException !!! = {}",throwable.getMessage());
    }
}
