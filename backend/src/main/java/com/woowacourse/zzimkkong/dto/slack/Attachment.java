package com.woowacourse.zzimkkong.dto.slack;

public class Attachment {
    private String fallback;
    private String color;
    private String pretext;
    private String title;
    private String titleLink;
    private SlackResponse slackResponse;

    public Attachment() {
    }

    private Attachment(
            final String fallback,
            final String color,
            final String pretext,
            final String title,
            final String titleLink,
            final SlackResponse slackResponse) {
        this.fallback = fallback;
        this.color = color;
        this.pretext = pretext;
        this.title = title;
        this.titleLink = titleLink;
        this.slackResponse = slackResponse;
    }

    public static Attachment of(String fallback, String color, String pretext, String title, String titleLink, SlackResponse slackResponse) {
        return new Attachment(fallback, color, pretext, title, titleLink, slackResponse);
    }

    @Override
    public String toString() {
        return "{" +
                "\"fallback\" : \"" + fallback + "\"" +
                ", \"color\" : \"" + color + "\"" +
                ", \"pretext\" : \"" + pretext + "\"" +
                ", \"title\" : \"" + title + "\"" +
                ", \"title_link\" : \"" + titleLink + "\"" +
                ", \"text\" : \"" + slackResponse + "\"" +
                "}";
    }
}
