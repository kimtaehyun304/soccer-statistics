package com.daelim.sfa.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LeagueNameSeasonDto {

    private String leagueName;

    @NotNull
    private int leagueSeason;

    public LeagueNameSeasonDto() {
        leagueSeason = 2024;
    }
}
