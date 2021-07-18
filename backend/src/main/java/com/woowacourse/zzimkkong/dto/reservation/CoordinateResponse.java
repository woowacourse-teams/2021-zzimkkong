package com.woowacourse.zzimkkong.dto.reservation;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CoordinateResponse {
    private int x;
    private int y;

    public CoordinateResponse() {

    }

    private CoordinateResponse(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static CoordinateResponse of(String coordinate) {
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
