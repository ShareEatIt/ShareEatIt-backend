package com.carpBread.shareEatIt.global.entity;

import lombok.Getter;

@Getter
public enum Period {
    WEEK("week"), MONTH("month");
    private String value;

    Period(String value) {
        this.value = value;
    }
}
