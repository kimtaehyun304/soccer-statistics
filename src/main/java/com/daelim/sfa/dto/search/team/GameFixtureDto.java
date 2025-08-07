package com.daelim.sfa.dto.search.team;

import com.daelim.sfa.domain.game.GameFixture;
import com.daelim.sfa.domain.game.HomeAway;
import com.daelim.sfa.domain.team.Team;
import com.daelim.sfa.domain.team.Venue;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class GameFixtureDto {

    private String referee;

    // UTC 등
    private String timezone;

    private LocalDate date;

    private TeamDto team1;

    private TeamDto team2;

    // NULL -> 무승부
    // private Long winner_team_id;
    private String winnerTeamName;

    //검색 키워드 기준입니다.
    private HomeAway homeAway;

    public GameFixtureDto(GameFixture gameFixture, Team team) {
        referee = gameFixture.getReferee();
        timezone = gameFixture.getTimezone();
        date = gameFixture.getDate().toLocalDate();

        TeamDto team1 = new TeamDto(gameFixture.getTeam1(), gameFixture.getTeam1Goals());
        TeamDto team2 = new TeamDto(gameFixture.getTeam2(), gameFixture.getTeam2Goals());

        // 검색한 팀 왼쪽 정렬
        if(team.getId().equals(gameFixture.getTeam1().getId())) {
            this.team1 = team1;
            this.team2 = team2;
        } else {
            this.team1 = team2;
            this.team2 = team1;
        }

        //winner_team_id = gameFixture.getWinnerTeam() == null ? null : gameFixture.getWinnerTeam().getId();
        winnerTeamName = gameFixture.getWinnerTeam() == null ? null : gameFixture.getWinnerTeam().getName();
        homeAway = gameFixture.getHomeTeam().getId().equals(team.getId()) ? HomeAway.HOME : HomeAway.AWAY;
    }

}
