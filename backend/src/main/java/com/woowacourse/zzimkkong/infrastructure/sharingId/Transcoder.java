package com.woowacourse.zzimkkong.infrastructure.sharingId;

public interface Transcoder {
    String encode(String input);

    String decode(String input);
}
