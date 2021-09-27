package com.woowacourse.zzimkkong.infrastructure.sharingid;

public interface Transcoder {
    String encode(String input);

    String decode(String input);
}
