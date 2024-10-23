package com.daelim.sfa.dto;

import com.daelim.sfa.domain.game.GameFixture;
import com.daelim.sfa.domain.team.Team;
import com.daelim.sfa.dto.search.team.TeamDto;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ThisWeekGameFixtureDto {

    private LocalDate date;

    private TeamDto team1;

    private TeamDto team2;

    public ThisWeekGameFixtureDto(GameFixture gameFixture) {
        this.date = gameFixture.getDate();
        this.team1 = new TeamDto(gameFixture.getTeam1());
        this.team2 = new TeamDto(gameFixture.getTeam2());
    }
}
