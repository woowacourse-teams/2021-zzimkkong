package com.woowacourse.zzimkkong.dto.slack;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class Attachments {
    private static final String COLOR = "#FF7515";
    private static final String TITLE_LINK_MESSAGE = "예약링크 바로가기";
    private static final String GUEST_URI = "/guest/";

    private List<Attachment> messageBody;

    private Attachments(final List<Attachment> messageBody) {
        this.messageBody = messageBody;
    }

    public static Attachments createMessageOf(final SlackResponse slackResponse,
                                              final String titleLink) {
        Attachment attachment = Attachment.of(
                "🎉 예약 생성 알림 🎉",
                COLOR,
                "🎉 예약이 생성되었습니다.",
                TITLE_LINK_MESSAGE,
                titleLink + GUEST_URI + slackResponse.getSharingMapId(),
                slackResponse);
        return Attachments.from(attachment);
    }

    public static Attachments updateMessageOf(final SlackResponse slackResponse,
                                              final String titleLink) {
        Attachment attachment = Attachment.of(
                "✏️ 예약 수정 알림 ✏️",
                COLOR,
                "✏️ 예약이 수정되었습니다.",
                TITLE_LINK_MESSAGE,
                titleLink + GUEST_URI + slackResponse.getSharingMapId(),
                slackResponse);
        return Attachments.from(attachment);
    }

    public static Attachments deleteMessageOf(final SlackResponse slackResponse,
                                              final String titleLink) {
        Attachment attachment = Attachment.of(
                "🗑 예약 삭제 알림 🗑",
                COLOR,
                "🗑 예약이 삭제되었습니다.",
                TITLE_LINK_MESSAGE,
                titleLink + GUEST_URI + slackResponse.getSharingMapId(),
                slackResponse);
        return Attachments.from(attachment);
    }

    private static Attachments from(final Attachment attachment) {
        List<Attachment> attachments = new ArrayList<>();
        attachments.add(attachment);
        return new Attachments(attachments);
    }

    @Override
    public String toString() {
        return "{ \"attachments\" : " +
                messageBody +
                "}";
    }
}
