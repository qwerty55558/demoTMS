package com.myTMS.demo.dao.typeconst;

public enum WeightCategory {
    LIGHT(8000L),MEDIUM(10000L),HEAVY(20000L);

    private final Long weightPrice;

    public Long get() {
        return weightPrice;
    }

    WeightCategory(Long weightPrice) {
        this.weightPrice = weightPrice;
    }

}
