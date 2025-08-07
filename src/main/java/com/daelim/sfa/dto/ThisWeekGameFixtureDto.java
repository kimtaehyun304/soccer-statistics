package com.daelim.sfa.dto;

import com.daelim.sfa.domain.game.GameFixture;
import com.daelim.sfa.dto.team.TeamDto;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ThisWeekGameFixtureDto {

    private String timezone;

    private LocalDateTime date;

    private TeamDto team1;

    private TeamDto team2;

    public ThisWeekGameFixtureDto(GameFixture gameFixture) {
        timezone = "KST";
        //UTC -> KST 변환
        this.date = gameFixture.getDate().plusHours(9);
        this.team1 = new TeamDto(gameFixture.getTeam1());
        this.team2 = new TeamDto(gameFixture.getTeam2());
    }
}
