package com.daelim.sfa.dto;

import com.daelim.sfa.domain.player.PlayerComment;
import com.daelim.sfa.domain.team.TeamComment;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ResponseChildCommentDto {

    //private Long id;

    private String memberName;

    private String content;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public ResponseChildCommentDto(PlayerComment playerComment) {
        memberName = playerComment.getMember().getName();
        content = playerComment.getContent();
        createdAt = playerComment.getCreatedAt();
        updatedAt = playerComment.getUpdatedAt();
    }

    public ResponseChildCommentDto(TeamComment teamComment) {
        memberName = teamComment.getMember().getName();
        content = teamComment.getContent();
        createdAt = teamComment.getCreatedAt();
        updatedAt = teamComment.getUpdatedAt();
    }
}
