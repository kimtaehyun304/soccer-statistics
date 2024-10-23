package com.daelim.sfa.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@Getter
public class RequestCommentDto {

    @NotEmpty
    @Length(max = 500)
    String content;

    Long parentId;

}
