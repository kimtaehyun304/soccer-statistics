package com.daelim.sfa.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResponseCountAndCommentDto {

    private int count;

    private List<ResponseCommentDto> comments;

    public ResponseCountAndCommentDto(int count, List<ResponseCommentDto> commentDtos) {
        this.count = count;
        this.comments = commentDtos;
    }
}
