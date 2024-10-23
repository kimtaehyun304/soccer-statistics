package com.daelim.sfa.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
public class SignUpDto {

    @NotEmpty
    private String loginId;

    @NotEmpty
    private String password;

    @NotEmpty
    private String name;

}
