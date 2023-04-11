package com.woowacourse.zzimkkong.domain;

import com.woowacourse.zzimkkong.dto.space.SettingRequest;
import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import com.woowacourse.zzimkkong.exception.setting.InvalidOrderException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

import javax.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;
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

    private Settings(final List<Setting> settings) {
        this.settings = new ArrayList<>(settings);
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

        return toPrioritizedSettings(settings);
    }

    public static Settings toPrioritizedSettings(final List<Setting> settings) {
        Settings newSettings = new Settings(settings);
        newSettings.validateOrderConflict();
        newSettings.sortByPriorityOrder();
        return newSettings;
    }

    public static Settings toFlattenedSettings(final List<Setting> settings) {
        Settings newSettings = new Settings(settings);
        if (newSettings.isFlat()) {
            return newSettings;
        }
        newSettings.validateOrderConflict();
        newSettings.sortByPriorityOrder();
        newSettings.flatten();
        return newSettings;
    }

    public void add(final Setting setting) {
        if (isFlat()) {
            throw new ZzimkkongException();
        }
        settings.add(setting);
        validateOrderConflict();
        sortByPriorityOrder();
    }

    public void addAll(final List<Setting> newSettings) {
        if (isFlat()) {
            throw new ZzimkkongException();
        }
        settings.addAll(newSettings);
        validateOrderConflict();
        sortByPriorityOrder();
    }

    public Settings getSettingsByTimeSlotAndDayOfWeek(final TimeSlot timeSlot, final DayOfWeek dayOfWeek) {
        List<Setting> relevantSettings = this.settings.stream()
                .filter(setting -> setting.supports(timeSlot, dayOfWeek))
                .collect(Collectors.toList());

        return toFlattenedSettings(relevantSettings).getMergedSettings(
                EnabledDayOfWeek.from(
                        dayOfWeek.name().toLowerCase(Locale.ROOT)));
    }

    public boolean cannotAcceptDueToAvailableTime(final TimeSlot timeSlot, final DayOfWeek dayOfWeek) {
        return getAvailableTimeSlots(dayOfWeek).stream().allMatch(timeSlot::isNotWithin);
    }

    public boolean isEmpty() {
        return CollectionUtils.isEmpty(settings);
    }

    public boolean haveMultipleSettings() {
        return settings.size() >= 2;
    }

    /**
     * settings 중 예약이 불가능한 시간 대역을 반환한다
     */
    public List<TimeSlot> getUnavailableTimeSlots(final DayOfWeek dayOfWeek) {
        Settings flatSettings = toFlattenedSettings(this.settings).getMergedSettings(
                EnabledDayOfWeek.from(dayOfWeek.name().toLowerCase(Locale.ROOT)));

        List<TimeSlot> unavailableTimeSlots = new ArrayList<>();
        LocalTime unavailableStartTime = LocalTime.MIN;
        LocalTime unavailableEndTime = TimeSlot.MAX_TIME;
        for (int i = 0; i < flatSettings.settings.size(); i++) {
            TimeSlot settingTimeSlot = flatSettings.settings.get(i).getSettingTimeSlot();

            unavailableEndTime = settingTimeSlot.getStartTime();
            if (!unavailableStartTime.equals(unavailableEndTime)) {
                unavailableTimeSlots.add(TimeSlot.of(unavailableStartTime, unavailableEndTime));
            }

            unavailableStartTime = settingTimeSlot.getEndTime();
            unavailableEndTime = TimeSlot.MAX_TIME;

            if (i == flatSettings.settings.size() - 1) {
                unavailableTimeSlots.add(TimeSlot.of(unavailableStartTime, unavailableEndTime));
            }
        }

        if (unavailableTimeSlots.isEmpty()) {
            unavailableTimeSlots.add(TimeSlot.of(unavailableStartTime, unavailableEndTime));
        }

        return unavailableTimeSlots;
    }

    /**
     * settings 중 예약 가능한 시간 대역을 반환한다
     */
    private List<TimeSlot> getAvailableTimeSlots(final DayOfWeek dayOfWeek) {
        if (isFlat()) {
            return settings.stream()
                    .map(Setting::getSettingTimeSlot)
                    .collect(Collectors.toList());
        }
        return toFlattenedSettings(this.settings).getMergedSettings(
                EnabledDayOfWeek.from(
                        dayOfWeek.name().toLowerCase(Locale.ROOT)))
                .settings
                .stream()
                .map(Setting::getSettingTimeSlot)
                .collect(Collectors.toList());
    }

    /**
     * 2023.04.02 기준
     * 공간의 예약 조건은 서로 겹쳐질 (중복될) 수 있으며, 각각 우선순위 (order)를 가진다.
     * 우선순위가 높은 예약조건을 우선시하여 검증한다.
     * 비즈니스 로직을 단순화 시키기 위해, flatten 메서드로 겹쳐진 예약 조건들을 '동등한 우선순위를 가진 겹치지 않은 상태 (= flat 한 상태)' 로 변환한다.
     * <p>
     * flatten 과정을 거치면 settings 의 모든 setting 들은:
     * - {@link Setting#settingTimeSlot}, {@link Setting#enabledDayOfWeek} 두 조건이 서로 겹치지 않는다
     * - id = {@link Setting#FLAT_SETTING_ID} (실제 존재하는 세팅이 아닌 추상적인 transient entity 임을 명시하기 위함)
     * - order = {@link Setting#FLAT_PRIORITY_ORDER} (동등한 우선순위)
     * - settingStartTime 기준으로 오름차순 정렬된다
     *
     */
    public void flatten() {
        List<Setting> flatSettings = new ArrayList<>();
        for (Setting setting : settings) {
            List<Setting> exclusiveSettingSlots = setting.extractExclusiveSettingSlots(new ArrayList<>(flatSettings));
            flatSettings.addAll(exclusiveSettingSlots);
        }
        this.settings = flatSettings;
        sortByTime();
    }

    public String getSummary() {
        StringBuilder stringBuilder = new StringBuilder();
        for (EnabledDayOfWeek dayOfWeek : EnabledDayOfWeek.values()) {
            stringBuilder.append(getSummaryOn(dayOfWeek));
        }
        return stringBuilder.toString();
    }

    public String getSummaryOn(final EnabledDayOfWeek dayOfWeek) {
        List<Setting> settingsOnDayOfWeek = settings.stream()
                .filter(setting -> setting.getEnabledDayOfWeekList().contains(dayOfWeek))
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(settingsOnDayOfWeek)) {
            return "[" + dayOfWeek.getDisplayName() + "] 이용 불가" + LINE_SEPARATOR;
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[")
                .append(dayOfWeek.getDisplayName())
                .append("]")
                .append(LINE_SEPARATOR);

        boolean flat = isFlat();
        if (flat) {
            settingsOnDayOfWeek = new Settings(settingsOnDayOfWeek).getMergedSettings(dayOfWeek).settings;
        }
        for (Setting setting : settingsOnDayOfWeek) {
            stringBuilder.append(setting.toSummaryWithoutDayOfWeek(flat))
                    .append(LINE_SEPARATOR);
        }
        return stringBuilder.toString();
    }

    public Settings getMergedSettings(final EnabledDayOfWeek dayOfWeek) {
        if (CollectionUtils.isEmpty(this.settings)) {
            return new Settings();
        }

        Settings flatSettings = toFlattenedSettings(this.settings);

        List<Setting> mergedSettings = new ArrayList<>();
        Setting startSetting = flatSettings.settings.get(0);
        if (!flatSettings.haveMultipleSettings()) {
            return new Settings(
                    List.of(
                            startSetting.createSettingBasedOn(
                                    startSetting.getSettingTimeSlot(),
                                    dayOfWeek)));
        }

        for (int i = 1; i < flatSettings.settings.size(); i++) {
            Setting endSetting = flatSettings.settings.get(i);

            if (startSetting.canMergeIgnoringDayOfWeek(endSetting)) {
                startSetting = startSetting.createSettingBasedOn(
                        TimeSlot.of(startSetting.getSettingStartTime(), endSetting.getSettingEndTime()),
                        dayOfWeek);
            } else {
                mergedSettings.add(startSetting.createSettingBasedOn(startSetting.getSettingTimeSlot(), dayOfWeek));
                startSetting = endSetting;
            }

            if (i == flatSettings.settings.size() - 1) {
                mergedSettings.add(startSetting.createSettingBasedOn(startSetting.getSettingTimeSlot(), dayOfWeek));
            }
        }
        return new Settings(mergedSettings);
    }

    private void validateOrderConflict() {
        Set<Integer> uniquePriorities = settings.stream()
                .map(Setting::getPriorityOrder)
                .collect(Collectors.toSet());

        if (settings.size() != uniquePriorities.size()) {
            throw new InvalidOrderException(DUPLICATE_SETTING_ORDER_MESSAGE);
        }
    }

    private boolean isFlat() {
        return settings.size() >= 1 && settings.stream().allMatch(Setting::isFlattenedSetting);
    }

    public void clear() {
        settings.clear();
    }


    public void reverseSortByPriorityOrder() {
        settings.sort(Comparator.comparing(Setting::getPriorityOrder).reversed());
    }

    private void sortByPriorityOrder() {
        settings.sort(Comparator.comparing(Setting::getPriorityOrder));
    }

    private void sortByTime() {
        settings.sort(Comparator.comparing(Setting::getSettingStartTime));
    }

    @PostLoad
    public void postLoad() {
        sortByPriorityOrder();
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


