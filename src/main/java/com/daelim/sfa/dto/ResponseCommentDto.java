package com.daelim.sfa.dto;

import com.daelim.sfa.domain.player.PlayerComment;
import com.daelim.sfa.domain.team.TeamComment;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class ResponseCommentDto {

    private Long id;

    private String memberName;

    private String content;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    //@JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<ResponseChildCommentDto> children = new ArrayList<>();

    public ResponseCommentDto(PlayerComment playerComment) {
        id = playerComment.getId();
        memberName = playerComment.getMember().getName();
        content = playerComment.getContent();
        createdAt = playerComment.getCreatedAt();
        updatedAt = playerComment.getUpdatedAt();
        children = playerComment.getChildren().stream().map(ResponseChildCommentDto::new).toList();
    }

    public ResponseCommentDto(TeamComment teamComment) {
        id = teamComment.getId();
        memberName = teamComment.getMember().getName();
        content = teamComment.getContent();
        createdAt = teamComment.getCreatedAt();
        updatedAt = teamComment.getUpdatedAt();
        children = teamComment.getChildren().stream().map(ResponseChildCommentDto::new).toList();
    }

}
