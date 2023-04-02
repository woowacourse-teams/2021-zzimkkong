package com.woowacourse.zzimkkong.domain;

/**
 * {@link SettingViewType#STACK}: 맵 관리자가 설정한 예약 조건 그대로 보여준다 (겹치는 조건 O). i.e. 관리자들을 위한 view
 * {@link SettingViewType#FLAT}: 예약 조건을 평탄화 하여 보여준다 (겹치는 조건 X). i.e. 예약자들을 위한 view
 * - {@link com.woowacourse.zzimkkong.domain.Settings#flatten()} 메서드 참고
 */
public enum SettingViewType {
    STACK,
    FLAT
}
