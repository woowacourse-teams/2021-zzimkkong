package com.woowacourse.zzimkkong.repository;

import com.woowacourse.zzimkkong.config.logaspect.LogMethodExecutionTime;
import com.woowacourse.zzimkkong.domain.Map;
import com.woowacourse.zzimkkong.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@LogMethodExecutionTime(group = "repository")
public interface MapRepository extends JpaRepository<Map, Long> {
    List<Map> findAllByMember(final Member member);

    @Query(value = "select m from Map m inner join fetch m.member",
            countQuery = "select count(m) from Map m")
    Page<Map> findAllByFetch(Pageable pageable);

    @Query("select distinct m from Map m inner join fetch m.member left outer join fetch m.spaces where m.id = :id")
    Optional<Map> findByIdFetch(@Param("id") Long id);
}
