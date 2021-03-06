package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.*;
import com.woowacourse.zzimkkong.dto.map.MapCreateResponse;
import com.woowacourse.zzimkkong.dto.map.MapCreateUpdateRequest;
import com.woowacourse.zzimkkong.dto.map.MapFindAllResponse;
import com.woowacourse.zzimkkong.dto.map.MapFindResponse;
import com.woowacourse.zzimkkong.dto.member.LoginEmailDto;
import com.woowacourse.zzimkkong.exception.authorization.NoAuthorityOnMapException;
import com.woowacourse.zzimkkong.exception.map.InvalidAccessLinkException;
import com.woowacourse.zzimkkong.exception.space.ReservationExistOnSpaceException;
import com.woowacourse.zzimkkong.infrastructure.sharingid.SharingIdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.woowacourse.zzimkkong.Constants.*;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

class MapServiceTest extends ServiceTest {
    @Autowired
    private MapService mapService;
    private Member pobi;
    private LoginEmailDto pobiEmail;
    private Map luther;
    private Map smallHouse;
    private Long lutherId;

    @BeforeEach
    void setUp() {
        pobi = new Member(EMAIL, PW, ORGANIZATION);
        pobiEmail = LoginEmailDto.from(EMAIL);
        luther = new Map(1L, LUTHER_NAME, MAP_DRAWING_DATA, MAP_SVG, pobi);
        smallHouse = new Map(2L, SMALL_HOUSE_NAME, MAP_DRAWING_DATA, MAP_SVG, pobi);
        lutherId = luther.getId();

        Setting beSetting = Setting.builder()
                .availableTimeSlot(TimeSlot.of(
                        BE_AVAILABLE_START_TIME,
                        BE_AVAILABLE_END_TIME))
                .reservationTimeUnit(BE_RESERVATION_TIME_UNIT)
                .reservationMinimumTimeUnit(BE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(BE_RESERVATION_MAXIMUM_TIME_UNIT)
                .reservationEnable(BE_RESERVATION_ENABLE)
                .enabledDayOfWeek(BE_ENABLED_DAY_OF_WEEK)
                .build();

        Space be = Space.builder()
                .id(1L)
                .name(BE_NAME)
                .map(luther)
                .description(BE_DESCRIPTION)
                .area(SPACE_DRAWING)
                .setting(beSetting)
                .build();

        Setting feSetting = Setting.builder()
                .availableTimeSlot(TimeSlot.of(
                        FE_AVAILABLE_START_TIME,
                        FE_AVAILABLE_END_TIME))
                .reservationTimeUnit(FE_RESERVATION_TIME_UNIT)
                .reservationMinimumTimeUnit(FE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(FE_RESERVATION_MAXIMUM_TIME_UNIT)
                .reservationEnable(FE_RESERVATION_ENABLE)
                .enabledDayOfWeek(FE_ENABLED_DAY_OF_WEEK)
                .build();

        Space fe = Space.builder()
                .id(2L)
                .name(FE_NAME)
                .color(FE_COLOR)
                .map(luther)
                .description(FE_DESCRIPTION)
                .area(SPACE_DRAWING)
                .setting(feSetting)
                .build();
    }

    @Autowired
    private SharingIdGenerator sharingIdGenerator;

    @Test
    @DisplayName("??? ?????? ?????? ???, ????????? ????????? ???????????? ?????? ????????????.")
    void create() {
        //given, when
        MapCreateUpdateRequest mapCreateUpdateRequest = new MapCreateUpdateRequest(luther.getName(), luther.getMapDrawing(), MAP_SVG);
        given(members.findByEmail(anyString()))
                .willReturn(Optional.of(pobi));
        given(maps.save(any(Map.class)))
                .willReturn(luther);

        //then
        MapCreateResponse mapCreateResponse = mapService.saveMap(mapCreateUpdateRequest, pobiEmail);
        assertThat(mapCreateResponse.getId()).isEqualTo(luther.getId());
    }

    @Test
    @DisplayName("??? ?????? ?????? ???, mapId??? ???????????? ?????? ????????????.")
    void find() {
        // given
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));

        //when
        MapFindResponse mapFindResponse = mapService.findMap(luther.getId(), pobiEmail);

        //then
        assertThat(mapFindResponse)
                .usingRecursiveComparison()
                .isEqualTo(MapFindResponse.of(luther, sharingIdGenerator.from(luther)));
    }

    @Test
    @DisplayName("?????? ??? ?????? ?????? ???, member??? ?????? ?????? ?????? ????????????.")
    void findAll() {
        //given
        List<Map> expectedMaps = List.of(luther, smallHouse);
        given(members.findByEmailWithFetchMaps(anyString()))
                .willReturn(Optional.of(pobi));

        //when
        MapFindAllResponse mapFindAllResponse = mapService.findAllMaps(pobiEmail);

        //then
        assertThat(mapFindAllResponse).usingRecursiveComparison()
                .isEqualTo(expectedMaps.stream()
                        .map(map -> MapFindResponse.of(map, sharingIdGenerator.from(map)))
                        .collect(collectingAndThen(toList(), mapFindResponses -> MapFindAllResponse.of(mapFindResponses, pobi))));
    }

    @Test
    @DisplayName("??? ?????? ????????? ???????????? ????????????.")
    void update() {
        //given
        MapCreateUpdateRequest mapCreateUpdateRequest = new MapCreateUpdateRequest("????????? ?????????", luther.getMapDrawing(), MAP_SVG);
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));

        //when, then
        assertDoesNotThrow(() -> mapService.updateMap(luther.getId(), mapCreateUpdateRequest, pobiEmail));
    }

    @Test
    @DisplayName("????????? ?????? ???????????? ?????? ??????????????? ??? ?????? ????????? ????????????.")
    void updateManagerException() {
        //given
        LoginEmailDto anotherEmail = LoginEmailDto.from(NEW_EMAIL);
        MapCreateUpdateRequest mapCreateUpdateRequest = new MapCreateUpdateRequest("????????? ?????????", luther.getMapDrawing(), MAP_SVG);

        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));

        // when, then
        assertThatThrownBy(() -> mapService.updateMap(lutherId, mapCreateUpdateRequest, anotherEmail))
                .isInstanceOf(NoAuthorityOnMapException.class);
    }

    @Test
    @DisplayName("??? ?????? ?????? ???, ????????? ???????????? ????????? ????????? ????????????.")
    void delete() {
        //given
        given(maps.findByIdFetch(anyLong()))
                .willReturn(Optional.of(luther));
        given(reservations.existsBySpaceIdAndReservationTimeEndTimeAfter(anyLong(), any(LocalDateTime.class)))
                .willReturn(false);

        //when, then
        assertDoesNotThrow(() -> mapService.deleteMap(luther.getId(), pobiEmail));
    }

    @Test
    @DisplayName("??? ?????? ?????? ???, ????????? ???????????? ????????? ????????? ????????? ????????????.")
    void deleteExistReservationException() {
        //given
        given(members.findByEmail(anyString()))
                .willReturn(Optional.of(pobi));
        given(maps.findByIdFetch(anyLong()))
                .willReturn(Optional.of(luther));
        given(reservations.existsBySpaceIdAndReservationTimeEndTimeAfter(anyLong(), any(LocalDateTime.class)))
                .willReturn(true);

        //when, then
        assertThatThrownBy(() -> mapService.deleteMap(lutherId, pobiEmail))
                .isInstanceOf(ReservationExistOnSpaceException.class);
    }

    @Test
    @DisplayName("Sharing Id????????? Map??? ?????? ??? ??????.")
    void findMapBySharingId() {
        // given
        String sharingId = sharingIdGenerator.from(luther);
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));

        // when
        MapFindResponse actual = mapService.findMapBySharingId(sharingId);
        MapFindResponse expected = MapFindResponse.of(luther, sharingIdGenerator.from(luther));

        // then
        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("????????? Sharing Id??? ???????????? ????????? ????????????.")
    void findMapByWrongSharingId() {
        // given
        String wrongSharingId = "zzimkkong";

        // when, then
        assertThatThrownBy(() -> mapService.findMapBySharingId(wrongSharingId))
                .isInstanceOf(InvalidAccessLinkException.class);
    }
}
