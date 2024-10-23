package com.daelim.sfa.dto.search.team;

import com.daelim.sfa.domain.team.TeamStatistics;
import jakarta.persistence.Column;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TeamStatisticsDto {

    private int played;

    private int wins;

    private int losses;

    private int draws;

    // 득점
    private int goalTotal;

    // 실점
    private int againstTotal;

    private int yellowTotal;

    private int redTotal;

    public TeamStatisticsDto(TeamStatistics teamStatistics) {
        played = teamStatistics.getFixtures().getPlayed();
        wins = teamStatistics.getFixtures().getWins();
        losses = teamStatistics.getFixtures().getLosses();
        draws = teamStatistics.getFixtures().getDraws();
        goalTotal = teamStatistics.getGoals().getForTotal();
        againstTotal = teamStatistics.getGoals().getAgainstTotal();
        yellowTotal = teamStatistics.getCards().getYellowTotal();
        redTotal = teamStatistics.getCards().getRedTotal();
    }

}
