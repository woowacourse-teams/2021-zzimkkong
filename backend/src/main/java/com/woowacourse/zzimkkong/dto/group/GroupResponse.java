package com.woowacourse.zzimkkong.dto.group;

import com.woowacourse.zzimkkong.domain.Group;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GroupResponse {
    private String name;        // "COACH"
    private String displayName; // "코치"
    private String color;       // "#3B82F6"

    public static GroupResponse from(Group group) {
        return new GroupResponse(
                group.name(),
                group.getDisplayName(),
                group.getColor()
        );
    }
}
