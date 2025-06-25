package com.myTMS.demo.dto;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class MailTemplate {
    private String title;
    private String to;
    private String templateName;
    private Map<String, String> values;

    protected MailTemplate(MailBuilder builder){
        this.title = builder.title;
        this.to = builder.to;
        this.templateName = builder.templateName;
        this.values = new HashMap<>(builder.values); // values 초기화
    }

    public static class MailBuilder {
        private String title;
        private String to;
        private String templateName;
        private Map<String, String> values = new HashMap<>(); // values 초기화

        public MailBuilder title(String title){
            this.title = title;
            return this;
        }

        public MailBuilder to(String to) {
            this.to = to;
            return this;
        }

        public MailBuilder templateName(String templateName) {
            this.templateName = templateName;
            return this;
        }

        public MailBuilder values(Map<String, String> values) {
            this.values.putAll(values); // 기존 값에 추가
            return this;
        }

        public MailTemplate build(){
            return new MailTemplate(this);
        }
    }

    @Override
    public String toString() {
        return "MailTemplate{" +
                "title='" + title + '\'' +
                ", to='" + to + '\'' +
                ", templateName='" + templateName + '\'' +
                ", values=" + values +
                '}';
    }
}