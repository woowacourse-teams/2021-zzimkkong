package com.woowacourse.zzimkkong.dto.slack;

import com.woowacourse.zzimkkong.domain.Reservation;

import java.util.ArrayList;
import java.util.List;

public class Attachments {
    private List<Attachment> attachments;
    private static final String COLOR = "#FF7515";
    private static final String TITLE_LINK = "https://zzimkkong.o-r.kr/";

    public Attachments() {
    }

    private Attachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public static Attachments updateMessageFrom(Reservation reservation) {
        Attachment attachment = Attachment.of(
                "✏️ 예약 수정 알림 ✏️",
                COLOR,
                "✏️ 예약이 수정되었습니다.",
                "변경된 예약내용",
                TITLE_LINK,
                Contents.from(reservation));
        return Attachments.from(attachment);
    }

    public static Attachments deleteMessageFrom(Reservation reservation) {
        Attachment attachment = Attachment.of(
                "🗑 예약 삭제 알림 🗑",
                COLOR,
                "🗑 예약이 삭제되었습니다.",
                "삭제된 예약내용",
                TITLE_LINK,
                Contents.from(reservation));
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
