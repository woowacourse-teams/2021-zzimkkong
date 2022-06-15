package com.woowacourse.zzimkkong.dto.map;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.NOTICE_MESSAGE;

@Getter
@NoArgsConstructor
public class NoticeCreateRequest {
    @Size(max = 100, message = NOTICE_MESSAGE)
    private String notice;

    public NoticeCreateRequest(final String notice) {
        this.notice = notice;
    }
}
