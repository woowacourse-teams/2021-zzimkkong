package com.woowacourse.zzimkkong.dto.space;

import com.woowacourse.zzimkkong.domain.Group;
import com.woowacourse.zzimkkong.domain.Reservation;
import com.woowacourse.zzimkkong.domain.Space;
import com.woowacourse.zzimkkong.dto.reservation.ReservationResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class SpaceEntryResponse {
    private Long spaceId;
    private String spaceName;
    private String spaceColor;
    private Boolean reservationEnable;
    private List<SettingResponse> settings;
    private List<Group> allowedGroups;
    private Long mapId;
    private String mapName;
    private String sharingMapId;
    private List<ReservationResponse> todayReservations;

    private SpaceEntryResponse(
            final Long spaceId,
            final String spaceName,
            final String spaceColor,
            final Boolean reservationEnable,
            final List<SettingResponse> settings,
            final List<Group> allowedGroups,
            final Long mapId,
            final String mapName,
            final String sharingMapId,
            final List<ReservationResponse> todayReservations) {
        this.spaceId = spaceId;
        this.spaceName = spaceName;
        this.spaceColor = spaceColor;
        this.reservationEnable = reservationEnable;
        this.settings = settings;
        this.allowedGroups = allowedGroups;
        this.mapId = mapId;
        this.mapName = mapName;
        this.sharingMapId = sharingMapId;
        this.todayReservations = todayReservations;
    }

    public static SpaceEntryResponse of(
            final Space space,
            final String sharingMapId,
            final List<Reservation> todayReservations) {
        List<SettingResponse> settingResponses = space.getSpaceSettings().getSettings()
                .stream()
                .map(SettingResponse::from)
                .collect(Collectors.toList());

        List<Group> allowedGroups = space.getAllowedGroups()
                .stream()
                .map(allowedGroup -> allowedGroup.getGroup())
                .collect(Collectors.toList());

        todayReservations.sort(Comparator.comparing(Reservation::getStartTime));
        List<ReservationResponse> reservationResponses = todayReservations.stream()
                .map(reservation -> ReservationResponse.from(reservation, null))
                .collect(Collectors.toList());

        return new SpaceEntryResponse(
                space.getId(),
                space.getName(),
                space.getColor(),
                space.getReservationEnable(),
                settingResponses,
                allowedGroups,
                space.getMap().getId(),
                space.getMap().getName(),
                sharingMapId,
                reservationResponses);
    }
}
