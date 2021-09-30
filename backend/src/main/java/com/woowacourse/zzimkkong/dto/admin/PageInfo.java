package com.woowacourse.zzimkkong.dto.admin;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class PageInfo {
    private int currentPage;
    private int lastPage;
    private int countPerPage;
    private long totalSize;

    private PageInfo(int currentPage, int lastPage, int countPerPage, long totalSize) {
        this.currentPage = currentPage;
        this.lastPage = lastPage;
        this.countPerPage = countPerPage;
        this.totalSize = totalSize;
    }

    public static PageInfo from(int currentPage, int lastPage, int countPerPage, long totalSize) {
        return PageInfo.fromNextPage(currentPage, lastPage, countPerPage, totalSize);
    }

    public static PageInfo fromNextPage(int currentPage, int lastPage, int countPerPage, long totalSize) {
        return new PageInfo(currentPage + 1, lastPage, countPerPage, totalSize);
    }
}

