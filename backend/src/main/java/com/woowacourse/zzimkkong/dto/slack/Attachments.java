package com.woowacourse.zzimkkong.dto.slack;

import java.util.ArrayList;
import java.util.List;

public class Attachments {
    private static final String COLOR = "#FF7515";
    private static final String TITLE_LINK = "https://zzimkkong.o-r.kr/";

    private List<Attachment> attachments;

    public Attachments() {
    }

    private Attachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public static Attachments updateMessageFrom(SlackResponse slackResponse) {
        Attachment attachment = Attachment.of(
                "✏️ 예약 수정 알림 ✏️",
                COLOR,
                "✏️ 예약이 수정되었습니다.",
                "변경된 예약내용",
                TITLE_LINK,
                slackResponse);
        return Attachments.from(attachment);
    }

    public static Attachments deleteMessageFrom(SlackResponse slackResponse) {
        Attachment attachment = Attachment.of(
                "🗑 예약 삭제 알림 🗑",
                COLOR,
                "🗑 예약이 삭제되었습니다.",
                "삭제된 예약내용",
                TITLE_LINK,
                slackResponse);
        return Attachments.from(attachment);
    }

    private static Attachments from(Attachment attachment) {
        List<Attachment> attachments = new ArrayList<>();
        attachments.add(attachment);
        return new Attachments(attachments);
    }

    @Override
    public String toString() {
        return "{ \"attachments\" : " +
                attachments +
                "}";
    }
}
