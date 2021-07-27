package com.woowacourse.zzimkkong.dto;

public class SvgDto {
    private String svg;
    private String name;

    public SvgDto() {

    }

    public SvgDto(String svg, String name) {
        this.svg = svg;
        this.name = name;
    }

    public String getSvg() {
        return svg;
    }

    public String getName() {
        return name;
    }
}
