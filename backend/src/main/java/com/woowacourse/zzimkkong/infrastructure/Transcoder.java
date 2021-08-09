package com.woowacourse.zzimkkong.infrastructure;

public interface Transcoder {
    String encode(String input);

    String decode(String input);
}
