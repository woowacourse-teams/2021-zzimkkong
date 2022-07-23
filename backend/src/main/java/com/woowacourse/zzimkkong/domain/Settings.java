package com.woowacourse.zzimkkong.domain;

import com.woowacourse.zzimkkong.dto.space.SpaceCreateUpdateRequest;
import com.woowacourse.zzimkkong.exception.setting.SettingConflictException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Embeddable
@NoArgsConstructor
public class Settings {
    public static final int MINIMUM_SETTING_COUNT = 1;

    @OneToMany(mappedBy = "space", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Setting> settings = new ArrayList<>();

    public Settings(final List<Setting> settings) {
        this.settings = settings.stream()
                .sorted(Comparator.comparing(Setting::getSettingStartTime))
                .collect(Collectors.toList());

        if (this.settings.size() <= MINIMUM_SETTING_COUNT) {
            return;
        }

        for (int i = 0; i < this.settings.size() - 1; i++) {
            Setting currentSetting = this.settings.get(i);
            Setting nextSetting = this.settings.get(i + 1);

            if (currentSetting.hasConflictWith(nextSetting)) {
                throw new SettingConflictException(currentSetting, nextSetting);
            }
        }
    }

    public static Settings of(final Space space, final SpaceCreateUpdateRequest spaceCreateUpdateRequest) {
        List<Setting> settings = spaceCreateUpdateRequest.getSettings()
                .stream()
                .map(settingRequest -> Setting.builder()
                        .settingTimeSlot(
                                TimeSlot.of(
                                        settingRequest.getSettingStartTime(),
                                        settingRequest.getSettingEndTime()))
                        .reservationTimeUnit(TimeUnit.from(settingRequest.getReservationTimeUnit()))
                        .reservationMinimumTimeUnit(TimeUnit.from(settingRequest.getReservationMinimumTimeUnit()))
                        .reservationMaximumTimeUnit(TimeUnit.from(settingRequest.getReservationMaximumTimeUnit()))
                        .enabledDayOfWeek(settingRequest.enabledDayOfWeekAsString())
                        .space(space)
                        .build())
                .collect(Collectors.toList());

        return new Settings(settings);
    }

    public void add(final Setting setting) {
        settings.add(setting);
    }

    public Settings getSettingsByTimeSlotAndDayOfWeek(final TimeSlot timeSlot, final DayOfWeek dayOfWeek) {
        List<Setting> filteredSettings = this.settings.stream()
                .filter(setting -> setting.supports(timeSlot, dayOfWeek))
                .collect(Collectors.toList());

        return new Settings(filteredSettings);
    }

    public boolean cannotAcceptDueToAvailableTime(final TimeSlot timeSlot) {
        return getAvailableTimeSlots().stream().anyMatch(timeSlot::isNotWithin);
    }

    public boolean isEmpty() {
        return CollectionUtils.isEmpty(settings);
    }

    public boolean haveMultipleSettings() {
        return settings.size() >= 2;
    }

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

    private List<TimeSlot> getAvailableTimeSlots() {
        if (!haveMultipleSettings()) {
            return settings.stream()
                    .map(Setting::getSettingTimeSlot)
                    .collect(Collectors.toList());
        }

        List<TimeSlot> availableTimeSlots = new ArrayList<>();
        LocalTime candidateStartTime = settings.get(0).getSettingStartTime();
        LocalTime candidateEndTime = settings.get(0).getSettingEndTime();
        for (int i = 1; i < settings.size(); i++) {
            TimeSlot candidateAvailableTimeSlot = TimeSlot.of(candidateStartTime, candidateEndTime);
            TimeSlot currentAvailableTimeSlot = settings.get(i).getSettingTimeSlot();

            if (candidateAvailableTimeSlot.isExtendableWith(currentAvailableTimeSlot)) {
                candidateEndTime = currentAvailableTimeSlot.getStartTime();
            } else {
                availableTimeSlots.add(candidateAvailableTimeSlot);

                candidateStartTime = currentAvailableTimeSlot.getStartTime();
                candidateEndTime = currentAvailableTimeSlot.getEndTime();
            }

            if (i == settings.size() - 1) {
                availableTimeSlots.add(TimeSlot.of(candidateStartTime, candidateEndTime));
            }
        }

        return availableTimeSlots;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < settings.size(); i++) {
            Setting setting = settings.get(i);
            stringBuilder.append(String.format("[예약 조건 %d]", i + 1));
            stringBuilder.append(System.getProperty("line.separator"));
            stringBuilder.append(setting);
            stringBuilder.append(System.getProperty("line.separator"));
        }
        return stringBuilder.toString();
    }
}


