package com.woowacourse.zzimkkong.repository;

import com.woowacourse.zzimkkong.config.logaspect.LogRegistry;
import com.woowacourse.zzimkkong.domain.Preset;
import org.springframework.data.jpa.repository.JpaRepository;

@LogRegistry(group = "repository")
public interface PresetRepository extends JpaRepository<Preset, Long> {
}
