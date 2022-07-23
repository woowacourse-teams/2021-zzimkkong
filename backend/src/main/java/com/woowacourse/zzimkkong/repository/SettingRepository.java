package com.woowacourse.zzimkkong.repository;

import com.woowacourse.zzimkkong.config.logaspect.FindInstanceAndCreateLogProxy;
import com.woowacourse.zzimkkong.domain.Setting;
import org.springframework.data.jpa.repository.JpaRepository;

@FindInstanceAndCreateLogProxy(group = "repository")
public interface SettingRepository extends JpaRepository<Setting, Long> {
}
