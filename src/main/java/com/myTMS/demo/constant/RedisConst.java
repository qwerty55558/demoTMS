package com.myTMS.demo.constant;

/**
 * redis 등록 시에 키값으로 사용
 */
public enum RedisConst {
    EMAIL("_EMAIL"),
    AUTH("_AUTH"),
    CATEGORY_CACHE_KEY("_CATEGORY_LIST"),
    ITEM_CACHE_KEY("_ITEM_LIST"),
    USER_ORDER_KEY("_USER_ORDER_KEY"),
    USER_TEMP_PASSWORD("_USER_TEMP_PASSWORD"),
    DEPARTMENT_CACHE_KEY("_DEPARTMENT_LIST"),
    EMERGENCY_ORDER_CACHE_KEY("_EM_LIST"),
    DASHBOARD_CACHE_KEY("_DASHBOARD_CHART")
    ;

    private final String label;

    RedisConst(String label) {
        this.label = label;
    }

    public String get(){
        return label;
    }

    public String get(String key) {
        return label + "_" + key;
    }
}
