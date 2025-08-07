package com.daelim.sfa.dto;

import com.daelim.sfa.domain.League;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LeagueDto {

    private Long leagueId;

    private String leagueName;

    public LeagueDto(League league) {
        this.leagueId = league.getId();
        this.leagueName = league.getName();
    }
}
