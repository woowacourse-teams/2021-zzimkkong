package com.woowacourse.zzimkkong.repository;

import com.woowacourse.zzimkkong.config.logaspect.FindInstanceAndCreateLogProxy;
import com.woowacourse.zzimkkong.domain.Space;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

@FindInstanceAndCreateLogProxy(group = "repository")
public interface SpaceRepository extends JpaRepository<Space, Long> {
    @Query(value = "select s from Space s inner join fetch s.map m " +
            "inner join fetch m.member " +
            "order by s.id",
            countQuery = "select count(s) from Space s")
    Page<Space> findAllByFetch(Pageable pageable);
}
