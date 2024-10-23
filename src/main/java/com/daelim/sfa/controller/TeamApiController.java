package com.daelim.sfa.controller;

import com.daelim.sfa.domain.League;
import com.daelim.sfa.domain.game.GameFixture;
import com.daelim.sfa.domain.team.Lineup;
import com.daelim.sfa.domain.team.Team;
import com.daelim.sfa.domain.team.TeamStatistics;
import com.daelim.sfa.dto.LeagueNameSeasonDto;
import com.daelim.sfa.dto.search.team.AutoCompleteTeam;
import com.daelim.sfa.dto.search.team.SearchTeamDto;
import com.daelim.sfa.repository.GameFixtureRepository;
import com.daelim.sfa.repository.LeagueRepository;
import com.daelim.sfa.repository.team.LineupRepository;
import com.daelim.sfa.repository.team.TeamRepository;
import com.daelim.sfa.repository.team.TeamStatisticsRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TeamApiController {

    private final TeamRepository teamRepository;
    private final TeamStatisticsRepository teamStatisticsRepository;
    private final LineupRepository lineupRepository;
    private final GameFixtureRepository gameFixtureRepository;
    private final LeagueRepository leagueRepository;

    @Operation(summary = "한 팀의 정보, 통계, 포메이션 ,경기 전적 조회", description = "")
    @Parameter(name = "teamName", description = "영문 이름으로 검색하고, 대소문자 구분하지 않습니다.", example = "Manchester United")
    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(mediaType = "application/json" , schema = @Schema(implementation = SearchTeamDto.class)))
    @GetMapping("/api/teams/{teamName}")
    public Object findTeam(@PathVariable String teamName, @ModelAttribute LeagueNameSeasonDto leagueNameSeasonDto) {

        Team team = teamRepository.findByName(teamName);

        if(team == null)
            return new ResponseEntity<>("조건에 맞는 검색 결과가 없습니다", HttpStatus.CONFLICT);

        TeamStatistics teamStatistics = teamStatisticsRepository.findByTeamNameAndAndSeason(teamName, leagueNameSeasonDto.getLeagueSeason());

        //TeamStatistics teamStatistics = teamStatisticsRepository.findWithLeagueByTeamIdAndLeagueIdAndSeason(team.getId(),foundLeague.getId(), leagueNameSeasonDto.getLeagueSeason());
        List<Lineup> lineups = lineupRepository.findAllByTeamStatisticsId(teamStatistics.getId());
        List<GameFixture> gameFixtures = gameFixtureRepository.findAllByTeamIdAndLeagueIdAndSeason(team.getId(), teamStatistics.getLeague().getId(), leagueNameSeasonDto.getLeagueSeason());

        return SearchTeamDto.builder().team(team).teamStatistics(teamStatistics).lineups(lineups).gameFixtures(gameFixtures).build();
    }

    @Operation(summary = "팀 리스트 조회", description = "연관된 팀을 조회합니다.")
    @Parameter(name = "teamName", description = "팀 영문 이름", example = "Manchester United")
    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = AutoCompleteTeam.class))))
    @GetMapping("/api/teams")
    public Object findTeams(@RequestParam String teamName){

        int maxResults = 7;
        List<Team> teams = teamRepository.findAllByName(teamName, maxResults);

        if(teams.isEmpty())
            return new ResponseEntity<>("조건에 맞는 검색 결과가 없습니다", HttpStatus.CONFLICT);

        return teams.stream().map(AutoCompleteTeam::new).toList();
    }

}
