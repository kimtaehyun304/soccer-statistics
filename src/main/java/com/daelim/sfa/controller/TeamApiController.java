package com.daelim.sfa.controller;

import com.daelim.sfa.domain.game.GameFixture;
import com.daelim.sfa.domain.team.Team;
import com.daelim.sfa.domain.team.TeamStatistics;
import com.daelim.sfa.dto.LeagueIdSeasonDto;
import com.daelim.sfa.dto.team.AutoCompleteTeam;
import com.daelim.sfa.dto.team.SearchTeamDto;
import com.daelim.sfa.dto.team.SquadDto;
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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TeamApiController {

    private final TeamRepository teamRepository;
    private final TeamStatisticsRepository teamStatisticsRepository;
    private final GameFixtureRepository gameFixtureRepository;

    @Operation(summary = "한 팀의 정보, 통계, 포메이션 ,경기 전적 조회", description = "팀 PK 로 검색합니다")
    @Parameter(name = "teamId", description = "", example = "33")
    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(mediaType = "application/json" , schema = @Schema(implementation = SearchTeamDto.class)))
    @GetMapping("/api/teams/{teamId}")
    public Object findTeam(@PathVariable Long teamId, @ModelAttribute LeagueIdSeasonDto leagueIdSeasonDto) {

        Team team = teamRepository.findById(teamId);

        if(team == null)
            return new ResponseEntity<>("조건에 맞는 검색 결과가 없습니다", HttpStatus.CONFLICT);

        List<Integer> seasons = teamStatisticsRepository.findSeasonsByTeamId(teamId);

        TeamStatistics teamStatistics = teamStatisticsRepository.findWithLineUpByTeamIdAndAndSeason(teamId, leagueIdSeasonDto.getLeagueSeason());
        List<GameFixture> gameFixtures = gameFixtureRepository.findAllByTeamIdAndLeagueIdAndSeason(team.getId(), teamStatistics.getLeague().getId(), leagueIdSeasonDto.getLeagueSeason());
        return SearchTeamDto.builder().team(team).seasons(seasons).teamStatistics(teamStatistics).lineups(teamStatistics.getLineups()).gameFixtures(gameFixtures).build();
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

    // 볼 수 있는 스쿼드 조회
    @GetMapping("/api/teams/squad")
    public Object findSquad() {

        List<TeamStatistics> foundTeamStatisticsList = teamStatisticsRepository.findAllByLeagueIdAndSeason(39L, 2024);
        List<Long> teamIds = foundTeamStatisticsList.stream().map(t -> t.getTeam().getId()).toList();
        List<Team> teams = teamRepository.findAllInId(teamIds);

        if(teams.isEmpty())
            return new ResponseEntity<>("검색 결과가 없습니다", HttpStatus.CONFLICT);

        //AutoCompleteTeam를 쓰는 게 어색하지만, 바빠서 일단 씁니다.
        return teams.stream().map(AutoCompleteTeam::new).toList();
    }

    // 스쿼드 (팀 멤버)
    @GetMapping("/api/teams/{teamId}/squad")
    public Object findSquadsByTeamId(@PathVariable Long teamId) {

        Team team = teamRepository.findWithPlayersByTeamId(teamId);

        if(team == null)
            return new ResponseEntity<>("조건에 맞는 검색 결과가 없습니다", HttpStatus.CONFLICT);

        return SquadDto.builder().team(team).players(team.getPlayers()).build();
    }



}
