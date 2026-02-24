package com.woowacourse.zzimkkong.dto.admin;

import com.woowacourse.zzimkkong.domain.Group;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
public class MemberGroupUpdateRequest {
    @NotNull(message = "그룹은 필수입니다.")
    private Group group;

    public MemberGroupUpdateRequest(Group group) {
        this.group = group;
    }
}
