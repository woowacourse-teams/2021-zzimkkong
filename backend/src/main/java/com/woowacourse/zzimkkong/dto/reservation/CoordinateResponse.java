package com.woowacourse.zzimkkong.dto.reservation;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CoordinateResponse {
    private int x;
    private int y;

    public CoordinateResponse() {

    }

    private CoordinateResponse(final int x, final int y) {
        this.x = x;
        this.y = y;
    }

    public static CoordinateResponse from(String coordinate) {
        // todo 컬럼 삭제할 때 삭제하기
        if (coordinate == null) {
            return null;
        }

        List<Integer> coordinateSet = Arrays.stream(coordinate.split(", "))
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        return new CoordinateResponse(coordinateSet.get(0), coordinateSet.get(1));
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return x + ", " + y;
    }
}
