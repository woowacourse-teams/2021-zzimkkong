package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.dto.admin.*;
import com.woowacourse.zzimkkong.dto.map.MapFindResponse;
import com.woowacourse.zzimkkong.dto.member.MemberFindResponse;
import com.woowacourse.zzimkkong.dto.member.TokenResponse;
import com.woowacourse.zzimkkong.dto.reservation.ReservationResponse;
import com.woowacourse.zzimkkong.dto.space.SpaceFindDetailWithIdResponse;
import com.woowacourse.zzimkkong.exception.member.PasswordMismatchException;
import com.woowacourse.zzimkkong.infrastructure.auth.JwtUtils;
import com.woowacourse.zzimkkong.infrastructure.sharingid.SharingIdGenerator;
import com.woowacourse.zzimkkong.repository.MapRepository;
import com.woowacourse.zzimkkong.repository.MemberRepository;
import com.woowacourse.zzimkkong.repository.ReservationRepository;
import com.woowacourse.zzimkkong.repository.SpaceRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Transactional(readOnly = true)
public class AdminService {

    private final String id;
    private final String pwd;

    private final JwtUtils jwtUtils;
    private final MemberRepository members;
    private final MapRepository maps;
    private final SpaceRepository spaces;
    private final ReservationRepository reservations;
    private final SharingIdGenerator sharingIdGenerator;

    public AdminService(@Value("${admin.id}") String adminId,
                        @Value("${admin.pwd}") String adminPwd,
                        final JwtUtils jwtUtils,
                        final MemberRepository members,
                        final MapRepository maps,
                        final SpaceRepository spaces,
                        final ReservationRepository reservations,
                        final SharingIdGenerator sharingIdGenerator) {
        id = adminId;
        pwd = adminPwd;
        this.jwtUtils = jwtUtils;
        this.members = members;
        this.maps = maps;
        this.spaces = spaces;
        this.reservations = reservations;
        this.sharingIdGenerator = sharingIdGenerator;
    }

    public TokenResponse login(final String id, final String password) {
        if (!id.equals(this.id) || !password.equals(pwd)) {
            throw new PasswordMismatchException();
        }
        String token = issueToken(id);

        return TokenResponse.from(token);
    }

    private String issueToken(final String id) {
        Map<String, Object> payload = JwtUtils.payloadBuilder()
                .setSubject(id)
                .build();

        return jwtUtils.createToken(payload);
    }

    public MembersResponse findMembers(Pageable pageable) {
        Page<MemberFindResponse> allMembers = members.findAll(pageable)
                .map(MemberFindResponse::from);

        return MembersResponse.from(allMembers.getContent(), makePageInfo(allMembers));
    }

    public MapsResponse findMaps(Pageable pageable) {
        Page<MapFindResponse> allMaps = maps.findAll(pageable)
                .map(map -> MapFindResponse.ofAdmin(map, sharingIdGenerator.from(map)));

        return MapsResponse.from(allMaps.getContent(), makePageInfo(allMaps));
    }

    public SpacesResponse findSpaces(Pageable pageable) {
        Page<SpaceFindDetailWithIdResponse> allSpaces = spaces.findAll(pageable)
                .map(SpaceFindDetailWithIdResponse::fromAdmin);

        return SpacesResponse.from(allSpaces.getContent(), makePageInfo(allSpaces));
    }

    public ReservationsResponse findReservations(Pageable pageable) {
        Page<ReservationResponse> allReservations = reservations.findAll(pageable)
                .map(ReservationResponse::fromAdmin);

        return ReservationsResponse.from(allReservations.getContent(), makePageInfo(allReservations));
    }

    private PageInfo makePageInfo(Page<?> data) {
        return PageInfo.from(
                data.getPageable().getPageNumber(),
                data.getTotalPages(),
                data.getPageable().getPageSize(),
                data.getTotalElements());
    }
}
