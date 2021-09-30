package com.woowacourse.zzimkkong.dto.admin;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

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

    public static PageInfo of(int currentPage, int lastPage, int countPerPage, long totalSize) {
        return ofNextPage(currentPage, lastPage, countPerPage, totalSize);
    }

    public static PageInfo ofNextPage(int currentPage, int lastPage, int countPerPage, long totalSize) {
        return new PageInfo(currentPage + 1, lastPage, countPerPage, totalSize);
    }

    public static PageInfo from(Page<?> data) {
        int pageNumber = data.getPageable().getPageNumber();
        int totalPages = data.getTotalPages();
        int pageSize = data.getPageable().getPageSize();
        long totalElements = data.getTotalElements();
        return ofNextPage(pageNumber, totalPages, pageSize, totalElements);
    }
}

