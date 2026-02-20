package com.woowacourse.zzimkkong.domain;

import lombok.Getter;

@Getter
public enum Group {
    COACH("코치", "#3B82F6"),
    NONE("없음", "#6B7280");

    private final String displayName;
    private final String color;

    Group(String displayName, String color) {
        this.displayName = displayName;
        this.color = color;
    }
}
