package com.daelim.sfa.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LeagueIdSeasonDto {

    private Long leagueId;

    @NotNull
    private Integer leagueSeason;

}
