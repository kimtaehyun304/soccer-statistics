package com.daelim.sfa.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaginationDto {

    int pageGroup;
    int firstPage;
    int lastPage;
    int pageCount; //행 갯수

    public PaginationDto(int page, int pageSize, int maxResults, Integer count) {
        pageGroup = (int) Math.ceil((double) page/pageSize);
        firstPage = (pageGroup-1)*pageSize + 1;
        lastPage = pageGroup*pageSize;
        pageCount = (int) Math.ceil((double) count/maxResults);
        if(lastPage>pageCount) lastPage = pageCount;
    }
}
