package com.myTMS.demo.constant;

/**
 * redis 등록 시에 밸류 값으로 사용
 */
public enum StatusCode {
    OK("_OK"),
    FAIL("_FAIL");

    private final String label;

    StatusCode(String label){
        this.label = label;
    }

    public String get() {
        return label;
    }

}
