package com.daelim.sfa.dto.search.team;

import com.daelim.sfa.domain.game.GameFixture;
import com.daelim.sfa.domain.team.Lineup;
import com.daelim.sfa.domain.team.Team;
import com.daelim.sfa.domain.team.TeamStatistics;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class SearchTeamDto {

    private TeamInformationDto information;

    //private VenueDto venue;

    private String leagueName;

    private int leagueSeason;

    private TeamStatisticsDto statistics;

    private List<LineupDto> lineups = new ArrayList<>();

    private List<GameFixtureDto> gameFixtures = new ArrayList<>();

    @Builder
    public SearchTeamDto(Team team, TeamStatistics teamStatistics, List<Lineup> lineups, List<GameFixture> gameFixtures) {
        information = new TeamInformationDto(team);
        leagueName = teamStatistics.getLeague().getName();
        leagueSeason = teamStatistics.getSeason();
        statistics = new TeamStatisticsDto(teamStatistics);
        this.lineups = lineups.stream().map(LineupDto::new).toList();
        this.gameFixtures = gameFixtures.stream().map(gameFixture -> new GameFixtureDto(gameFixture, team)).toList();
    }



}
