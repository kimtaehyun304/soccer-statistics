package com.daelim.sfa.controller;

import com.daelim.sfa.dto.LeagueIdSeasonDto;
import com.daelim.sfa.dto.ranking.PlayerRankingDto;
import com.daelim.sfa.dto.ranking.TeamRankingDto;
import com.daelim.sfa.repository.query.PlayerStatisticsQueryRepository;
import com.daelim.sfa.repository.query.TeamStatisticsQueryRepository;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RankingApiController {

    private final PlayerStatisticsQueryRepository playerStatisticsQueryRepository;
    private final TeamStatisticsQueryRepository teamStatisticsQueryRepository;

    @Operation(summary = "그래프 합계로 순위를 정하는 리그별 선수 랭킹 (1~100등)", description = "leagueName 생략시 리그를 구분하지 않습니다.")
    @Parameter(name = "leagueName", description = "리그 영문 이름", example = "Premier League, Serie A, Bundesliga, La Liga")
    @Parameter(name = "leagueSeason", description = "", example = "2023 or 2024")
    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PlayerRankingDto.class))))
    @GetMapping("/api/ranking/players")
    public Object findPlayerRanking(@ModelAttribute @Valid LeagueIdSeasonDto leagueIdSeasonDto, BindingResult bindingResult){

        if(bindingResult.hasErrors()) {
            StringBuilder message = new StringBuilder();
            for (FieldError fieldError : bindingResult.getFieldErrors())
                message.append(fieldError.getField()).append("는(은) ").append(fieldError.getDefaultMessage()).append(". ");
            return new ResponseEntity<>(message.toString(), HttpStatus.BAD_REQUEST);
        }

        List<PlayerRankingDto> playerRankingDtos = playerStatisticsQueryRepository.findAllByLeagueIdAndLeagueSeason(leagueIdSeasonDto.getLeagueId(), leagueIdSeasonDto.getLeagueSeason());

        if(playerRankingDtos.isEmpty())
            return new ResponseEntity<>("조건에 맞는 검색 결과가 없습니다", HttpStatus.CONFLICT);

        return playerRankingDtos;
    }

    @Operation(summary = "팀 랭킹(1~100등) 리스트 ", description = "")
    @Parameter(name = "leagueName", description = "리그 영문 이름", example = "Premier League, Serie A, Bundesliga, La Liga")
    @Parameter(name = "leagueSeason", description = "", example = "2023 or 2024")
    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = TeamRankingDto.class))))
    @GetMapping("/api/ranking/teams")
    public Object findTeamRanking(@ModelAttribute @Valid LeagueIdSeasonDto leagueIdSeasonDto, BindingResult bindingResult){

        if(bindingResult.hasErrors()) {
            StringBuilder message = new StringBuilder();
            for (FieldError fieldError : bindingResult.getFieldErrors())
                message.append(fieldError.getField()).append("는(은) ").append(fieldError.getDefaultMessage()).append(". ");
            return new ResponseEntity<>(message.toString(), HttpStatus.BAD_REQUEST);
        }

        List<TeamRankingDto> teamRankingDtos = teamStatisticsQueryRepository.findAllByLeagueNameAndLeagueSeason(leagueIdSeasonDto.getLeagueId(), leagueIdSeasonDto.getLeagueSeason());

        if(teamRankingDtos.isEmpty())
            return new ResponseEntity<>("조건에 맞는 검색 결과가 없습니다", HttpStatus.CONFLICT);

        return teamRankingDtos;
    }
}