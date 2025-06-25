package com.myTMS.demo.dao.wrapper;

import lombok.Getter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Locale;

@Getter
public class EmitterWrapper {
    private final SseEmitter emitter;
    private final Locale locale;

    public EmitterWrapper(SseEmitter emitter, Locale locale) {
        this.emitter = emitter;
        this.locale = locale;
    }
}

