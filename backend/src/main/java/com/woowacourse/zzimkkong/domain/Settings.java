package com.woowacourse.zzimkkong.domain;

import com.woowacourse.zzimkkong.dto.space.SettingRequest;
import com.woowacourse.zzimkkong.exception.setting.InvalidOrderException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

import javax.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.DUPLICATE_SETTING_ORDER_MESSAGE;
import static com.woowacourse.zzimkkong.infrastructure.message.MessageUtils.LINE_SEPARATOR;

@Getter
@Embeddable
@NoArgsConstructor
public class Settings {
    public static final int MINIMUM_SETTING_COUNT = 1;

    @OneToMany(mappedBy = "space", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Setting> settings = new ArrayList<>();

    public Settings(final List<Setting> settings) {
        this.settings = new ArrayList<>(settings);
        validateOrderConflict();
        sort();
    }

    public static Settings from(final List<SettingRequest> settingRequests) {
        List<Setting> settings = settingRequests.stream()
                .map(settingRequest -> Setting.builder()
                        .settingTimeSlot(
                                TimeSlot.of(
                                        settingRequest.getSettingStartTime(),
                                        settingRequest.getSettingEndTime()))
                        .reservationTimeUnit(TimeUnit.from(settingRequest.getReservationTimeUnit()))
                        .reservationMinimumTimeUnit(TimeUnit.from(settingRequest.getReservationMinimumTimeUnit()))
                        .reservationMaximumTimeUnit(TimeUnit.from(settingRequest.getReservationMaximumTimeUnit()))
                        .enabledDayOfWeek(settingRequest.enabledDayOfWeekAsString())
                        .priorityOrder(settingRequest.getPriorityOrder())
                        .build())
                .collect(Collectors.toList());

        return new Settings(settings);
    }

    public void add(final Setting setting) {
        settings.add(setting);
        validateOrderConflict();
        sort();
    }

    public void addAll(final List<Setting> newSettings) {
        settings.addAll(newSettings);
        validateOrderConflict();
        sort();
    }

    public Settings getSettingsByTimeSlotAndDayOfWeek(final TimeSlot timeSlot, final DayOfWeek dayOfWeek) {
        List<Setting> relevantSettingsInReversedOrder = this.settings.stream()
                .filter(setting -> setting.supports(timeSlot, dayOfWeek))
                .collect(Collectors.toList());

        return new Settings(relevantSettingsInReversedOrder);
    }

    public boolean cannotAcceptDueToAvailableTime(final TimeSlot timeSlot) {
        return getAvailableTimeSlots().stream().allMatch(timeSlot::isNotWithin);
    }

    public boolean isEmpty() {
        return CollectionUtils.isEmpty(settings);
    }

    public boolean haveMultipleSettings() {
        return settings.size() >= 2;
    }

    /**
     * settings 중 조건에 위배되는 예약이 불가능한 시간 대역을 반환한다
     */
    public List<TimeSlot> getUnavailableTimeSlots() {
        List<TimeSlot> unavailableTimeSlots = new ArrayList<>();
        LocalTime unavailableStartTime = LocalTime.MIN;
        LocalTime unavailableEndTime = TimeSlot.MAX_TIME;
        for (int i = 0; i < settings.size(); i++) {
            TimeSlot settingTimeSlot = settings.get(i).getSettingTimeSlot();

            unavailableEndTime = settingTimeSlot.getStartTime();
            if (!unavailableStartTime.equals(unavailableEndTime)) {
                unavailableTimeSlots.add(TimeSlot.of(unavailableStartTime, unavailableEndTime));
            }

            unavailableStartTime = settingTimeSlot.getEndTime();
            unavailableEndTime = TimeSlot.MAX_TIME;

            if (i == settings.size() - 1) {
                unavailableTimeSlots.add(TimeSlot.of(unavailableStartTime, unavailableEndTime));
            }
        }

        if (unavailableTimeSlots.isEmpty()) {
            unavailableTimeSlots.add(TimeSlot.of(unavailableStartTime, unavailableEndTime));
        }

        return unavailableTimeSlots;
    }

    /**
     * settings 중 조건에 위배되지 않는 예약 가능한 시간 대역을 반환한다
     */
    private List<TimeSlot> getAvailableTimeSlots() {
        if (!haveMultipleSettings()) {
            return settings.stream()
                    .map(Setting::getSettingTimeSlot)
                    .collect(Collectors.toList());
        }

        List<TimeSlot> availableTimeSlots = new ArrayList<>();
        LocalTime candidateStartTime = settings.get(0).getSettingStartTime();
        LocalTime candidateEndTime = settings.get(0).getSettingEndTime();
        TimeSlot candidateAvailableTimeSlot = TimeSlot.of(candidateStartTime, candidateEndTime);
        boolean onExtension;
        for (int i = 1; i < settings.size(); i++) {
            TimeSlot currentAvailableTimeSlot = settings.get(i).getSettingTimeSlot();

            if (candidateAvailableTimeSlot.isExtendableWith(currentAvailableTimeSlot)) {
                onExtension = true;
            } else {
                availableTimeSlots.add(candidateAvailableTimeSlot);
                onExtension = false;
            }

            if (!onExtension) {
                candidateStartTime = currentAvailableTimeSlot.getStartTime();
            }
            candidateEndTime = currentAvailableTimeSlot.getEndTime();
            candidateAvailableTimeSlot = TimeSlot.of(candidateStartTime, candidateEndTime);

            if (i == settings.size() - 1) {
                availableTimeSlots.add(TimeSlot.of(candidateStartTime, candidateEndTime));
            }
        }

        return availableTimeSlots;
    }

    public void clear() {
        settings.clear();
    }

    public void reverseSort() {
        settings.sort(Comparator.comparing(Setting::getPriorityOrder).reversed());
    }

    private void sort() {
        settings.sort(Comparator.comparing(Setting::getPriorityOrder));
    }

    private void validateOrderConflict() {
        Set<Integer> uniquePriorities = settings.stream()
                .map(Setting::getPriorityOrder)
                .collect(Collectors.toSet());

        if (settings.size() != uniquePriorities.size()) {
            throw new InvalidOrderException(DUPLICATE_SETTING_ORDER_MESSAGE);
        }
    }

    /**
     * 2023.04.02 기준
     * 공간의 예약 조건은 서로 겹쳐질 (중복될) 수 있으며, 각각 우선순위 (order)를 가진다.
     * 우선순위가 높은 예약조건을 우선시하여 검증한다.
     * 비즈니스 로직을 단순화 시키기 위해, flatten 메서드로 겹쳐진 예약 조건들을 '동등한 우선순위를 가진 겹치지 않은 상태 (= flat 한 상태)' 로 변환한다.
     *
     * flatten 과정을 거치면 settings 의 모든 setting 들은:
     * - {@link Setting#settingTimeSlot}, {@link Setting#enabledDayOfWeek} 두 조건이 서로 겹치지 않는다
     * - 모두 id 가 0 이 된다 (transient entity 임을 명시하기 위함)
     * - 모두 order 가 0 이 된다 (동등한 우선순위)
     * - settingStartTime 기준으로 오름차순 정렬된다
     */
    public void flatten() {
        List<Setting> flatSettings = new ArrayList<>();
        for (Setting setting : settings) {
            List<Setting> exclusiveSettingSlots = setting.extractExclusiveSettingSlots(new ArrayList<>(flatSettings));
            flatSettings.addAll(exclusiveSettingSlots);
        }
        flatSettings.sort(Comparator.comparing(Setting::getSettingStartTime));

        this.settings = flatSettings;
    }

    public String getSummary() {
        StringBuilder stringBuilder = new StringBuilder();
        for (EnabledDayOfWeek dayOfWeek : EnabledDayOfWeek.values()) {
            stringBuilder.append(getSummaryOn(dayOfWeek));
        }
        return stringBuilder.toString();
    }

    public String getSummaryOn(final EnabledDayOfWeek dayOfWeek) {
        List<Setting> relevantSettings = settings.stream()
                .filter(setting -> setting.getEnabledDayOfWeekList().contains(dayOfWeek))
                .collect(Collectors.toList());

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[")
                .append(dayOfWeek.getDisplayName())
                .append("]")
                .append(LINE_SEPARATOR);

        boolean flat = isFlat();
        for (Setting relevantSetting : relevantSettings) {
            stringBuilder.append(relevantSetting.toSummaryWithoutDayOfWeek(flat))
                    .append(LINE_SEPARATOR);
        }
        return stringBuilder.append(LINE_SEPARATOR).toString();
    }

    private boolean isFlat() {
        return settings.stream()
                .map(Setting::getPriorityOrder)
                .collect(Collectors.toSet())
                .size() == 1;
    }

    @PostLoad
    public void postLoad() {
        sort();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < settings.size(); i++) {
            Setting setting = settings.get(i);
            stringBuilder.append(String.format("[예약 조건 %d]", i + 1));
            stringBuilder.append(LINE_SEPARATOR);
            stringBuilder.append(setting);
            stringBuilder.append(LINE_SEPARATOR);
        }
        return stringBuilder.toString();
    }
}


