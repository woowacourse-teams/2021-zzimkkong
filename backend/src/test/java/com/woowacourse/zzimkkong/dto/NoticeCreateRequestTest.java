package com.woowacourse.zzimkkong.dto;

import com.woowacourse.zzimkkong.dto.map.NoticeCreateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Description;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.NOTICE_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;

class NoticeCreateRequestTest extends RequestTest {
    @Test
    @Description("100자 이상 공지사항이 들어오면 예외를 반환한다.")
    void noticeLengthOverSizeLimit() {
        NoticeCreateRequest noticeCreateRequest = new NoticeCreateRequest(
                "iamtenwordiamtenwordiamtenwordiamtenwordiamtenwordiamtenwordiamtenwordiamtenwordiamtenwordiamtenword1");

        assertThat(getConstraintViolations(noticeCreateRequest).stream()
                .anyMatch(violation -> violation.getMessage().equals(NOTICE_MESSAGE)))
                .isTrue();
    }
}
