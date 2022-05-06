package com.woowacourse.zzimkkong.dto.map;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NoticeCreateRequest {
    private String notice;

    public NoticeCreateRequest(final String notice) {
        this.notice = notice;
    }
}
