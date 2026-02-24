package com.woowacourse.zzimkkong.dto.space;

import com.woowacourse.zzimkkong.domain.Group;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
public class AllowedGroupRequest {
    @NotNull
    private Group group;

    public AllowedGroupRequest(final Group group) {
        this.group = group;
    }
}
