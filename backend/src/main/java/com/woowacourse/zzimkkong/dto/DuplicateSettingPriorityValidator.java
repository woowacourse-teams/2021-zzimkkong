package com.woowacourse.zzimkkong.dto;

import com.woowacourse.zzimkkong.dto.space.SettingRequest;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DuplicateSettingPriorityValidator implements ConstraintValidator<NotDuplicatedSettingPriority, List<SettingRequest>> {
    @Override
    public boolean isValid(final List<SettingRequest> value, final ConstraintValidatorContext context) {
        Set<Integer> uniquePriorities = value.stream()
                .map(SettingRequest::getPriority)
                .collect(Collectors.toSet());

        return value.size() == uniquePriorities.size();
    }
}
