package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.dto.admin.MapsResponse;
import com.woowacourse.zzimkkong.dto.admin.MembersResponse;
import com.woowacourse.zzimkkong.dto.admin.PageInfo;
import com.woowacourse.zzimkkong.dto.admin.SpacesResponse;
import com.woowacourse.zzimkkong.dto.map.MapFindResponse;
import com.woowacourse.zzimkkong.dto.member.MemberFindResponse;
import com.woowacourse.zzimkkong.dto.space.SpaceFindDetailWithIdResponse;
import com.woowacourse.zzimkkong.exception.member.PasswordMismatchException;
import com.woowacourse.zzimkkong.infrastructure.SharingIdGenerator;
import com.woowacourse.zzimkkong.repository.MapRepository;
import com.woowacourse.zzimkkong.repository.MemberRepository;
import com.woowacourse.zzimkkong.repository.SpaceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AdminService {
    private static final String ADMIN_ID = "zzimkkong";
    private static final String ADMIN_PWD = "zzimkkong1!";

    private final MemberRepository members;
    private final MapRepository maps;
    private final SpaceRepository spaces;
    private final SharingIdGenerator sharingIdGenerator;

    public AdminService(final MemberRepository members,
                        final MapRepository maps,
                        SpaceRepository spaces, final SharingIdGenerator sharingIdGenerator) {
        this.members = members;
        this.maps = maps;
        this.spaces = spaces;
        this.sharingIdGenerator = sharingIdGenerator;
    }

    public void login(final String id, final String password) {
        if (!id.equals(ADMIN_ID) || !password.equals(ADMIN_PWD)) {
            throw new PasswordMismatchException();
        }
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
                .map(SpaceFindDetailWithIdResponse::from);

        return SpacesResponse.from(allSpaces.getContent(), makePageInfo(allSpaces));
    }

    private PageInfo makePageInfo(Page<?> data) {
        return PageInfo.from(
                data.getPageable().getPageNumber(),
                data.getTotalPages(),
                data.getPageable().getPageSize(),
                data.getTotalElements());
    }
}
