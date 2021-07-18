package com.woowacourse.zzimkkong.dto.space;

import com.woowacourse.zzimkkong.domain.Space;

public class SpaceFindResponse {
    private Long id;
    private String name;
    private String textPosition;
    private String color;
    private String coordinate;

    private SpaceFindResponse() {
    }

    private SpaceFindResponse(final Long id, final String name, String textPosition, String color, String coordinate) {
        this.id = id;
        this.name = name;
        this.textPosition = textPosition;
        this.color = color;
        this.coordinate = coordinate;
    }

    public static SpaceFindResponse from(Space space) {
        return new SpaceFindResponse(
                space.getId(),
                space.getName(),
                space.getTextPosition(),
                space.getColor(),
                space.getCoordinate());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTextPosition() {
        return textPosition;
    }

    public String getColor() {
        return color;
    }

    public String getCoordinate() {
        return coordinate;
    }
}
