package com.woowacourse.zzimkkong.dto.slack;

public class Attachment {
    private String fallback;
    private String color;
    private String pretext;
    private String title;
    private String titleLink;
    private Contents contents;

    public Attachment() {
    }

    private Attachment(
            final String fallback,
            final String color,
            final String pretext,
            final String title,
            final String titleLink,
            final Contents contents) {
        this.fallback = fallback;
        this.color = color;
        this.pretext = pretext;
        this.title = title;
        this.titleLink = titleLink;
        this.contents = contents;
    }

    public static Attachment of(String fallback, String color, String pretext, String title, String titleLink, Contents contents) {
        return new Attachment(fallback, color, pretext, title, titleLink, contents);
    }

    @Override
    public String toString() {
        return "{" +
                "\"fallback\" : \"" + fallback + "\"" +
                ", \"color\" : \"" + color + "\"" +
                ", \"pretext\" : \"" + pretext + "\"" +
                ", \"title\" : \"" + title + "\"" +
                ", \"title_link\" : \"" + titleLink + "\"" +
                ", \"text\" : \"" + contents + "\"" +
                "}";
    }
}
