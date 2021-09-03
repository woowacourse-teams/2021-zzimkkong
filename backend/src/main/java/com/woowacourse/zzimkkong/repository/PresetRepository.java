package com.woowacourse.zzimkkong.repository;

import com.woowacourse.zzimkkong.domain.Preset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PresetRepository extends JpaRepository<Preset, Long> {
}
