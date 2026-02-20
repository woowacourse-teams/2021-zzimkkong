package com.woowacourse.zzimkkong.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
@Entity
public class Space {
    public static final String DELIMITER = ",";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = true, length = 25)
    private String color;

    @Column(nullable = true)
    private String area;

    @Column(nullable = false)
    private Boolean reservationEnable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "map_id", foreignKey = @ForeignKey(name = "fk_space_map"), nullable = false)
    private Map map;

    @Embedded
    @Builder.Default
    private Settings spaceSettings = new Settings();

    //TODO: Embedded로 고치고 Reservations Embeddable 클래스 생성하기 + 관련 로직 (e.g. 예약들 겹침 검증) Reservations 안으로 옮기기
    @OneToMany(mappedBy = "space", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private List<Reservation> reservations = new ArrayList<>();

    @OneToMany(mappedBy = "space", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<AllowedGroup> allowedGroups = new ArrayList<>();

    public Space(
            final Long id,
            final String name,
            final String color,
            final String area,
            final Boolean reservationEnable,
            final Map map,
            final Settings spaceSettings,
            final List<Reservation> reservations,
            final List<AllowedGroup> allowedGroups) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.area = area;
        this.reservationEnable = reservationEnable;
        this.spaceSettings = new Settings();
        this.map = map;
        this.reservations = reservations;
        this.allowedGroups = allowedGroups != null ? allowedGroups : new ArrayList<>();

        updateSpaceSettings(spaceSettings);

        if (this.map != null) {
            this.map.addSpace(this);
        }
    }

    public void update(final Space updateSpace) {
        this.name = updateSpace.name;
        this.color = updateSpace.color;
        this.area = updateSpace.area;
        this.reservationEnable = updateSpace.reservationEnable;

        updateSpaceSettings(updateSpace.getSpaceSettings());
        updateAllowedGroups(updateSpace.getAllowedGroups());
    }

    private void updateAllowedGroups(final List<AllowedGroup> updateAllowedGroups) {
        this.allowedGroups.clear();
        for (AllowedGroup allowedGroup : updateAllowedGroups) {
            allowedGroup.updateSpace(this);
            this.allowedGroups.add(allowedGroup);
        }
    }

    private void updateSpaceSettings(final Settings settings) {
        List<Setting> updateSettings = settings.getSettings();
        for (Setting setting : updateSettings) {
            setting.updateSpace(this);
        }
        this.spaceSettings.clear();
        this.spaceSettings.addAll(updateSettings);
    }

    public boolean isUnableToReserve() {
        return !reservationEnable;
    }

    public void addReservation(final Reservation reservation) {
        reservations.add(reservation);
    }

    public void addSetting(final Setting setting) {
        setting.updateSpace(this);
        spaceSettings.add(setting);
    }

    public boolean hasSameId(final Long spaceId) {
        return id.equals(spaceId);
    }

    public Settings getRelevantSettings(final TimeSlot timeSlot, final DayOfWeek dayOfWeek) {
        return spaceSettings.getSettingsByTimeSlotAndDayOfWeek(timeSlot, dayOfWeek);
    }

    public ServiceZone getServiceZone() {
        return map.getServiceZone();
    }

    public boolean isReservableBy(final Member member) {
        // allowedGroups가 비어있으면 모든 사용자 예약 가능
        if (allowedGroups.isEmpty()) {
            return true;
        }
        // 비로그인 사용자는 allowedGroups가 있으면 불가
        if (member == null) {
            return false;
        }
        // member의 group이 allowedGroups에 포함되어 있는지 확인
        return allowedGroups.stream()
                .anyMatch(allowedGroup -> allowedGroup.isGroup(member.getGroup()));
    }
}
