package com.daelim.sfa.controller;

import com.daelim.sfa.domain.player.Player;
import com.daelim.sfa.domain.player.PlayerStatistics;
import com.daelim.sfa.dto.LeagueNameSeasonDto;
import com.daelim.sfa.dto.search.player.AutoCompletePlayer;
import com.daelim.sfa.dto.search.player.SearchPlayerDto;
import com.daelim.sfa.repository.player.PlayerRepository;
import com.daelim.sfa.repository.player.PlayerStatisticsRepository;
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
public class PlayerApiController {

    private final PlayerRepository playerRepository;
    private final PlayerStatisticsRepository playerStatisticsRepository;

    @Operation(summary = "한 선수의 정보와 통계 조회", description = "영문 이름으로 검색하고, 대소문자 구분하지 않습니다.")
    @Parameter(name = "playerName", description = "", example = "Manuel Obafemi Akanji")
    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(mediaType = "application/json" , schema = @Schema(implementation = SearchPlayerDto.class)))
    @GetMapping("/api/players/{playerName}")
    public Object findPlayer(@PathVariable String playerName, @ModelAttribute LeagueNameSeasonDto leagueNameSeasonDto){

        Player player = playerRepository.findByName(playerName);
        
        if(player == null)
            return new ResponseEntity<>("조건에 맞는 검색 결과가 없습니다", HttpStatus.CONFLICT);

        List<PlayerStatistics> playerStatisticsList = playerStatisticsRepository.findAllWithLeagueByPlayerIdAndSeason(player.getId(), leagueNameSeasonDto.getLeagueSeason());

        return new SearchPlayerDto(player, playerStatisticsList);
    }

    @Operation(summary = "선수 리스트 조회", description = "연관된 선수 5명을 조회합니다.")
    @Parameter(name = "keyword", description = "선수 영문 이름", example = "Manuel Obafemi Akanji")
    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = AutoCompletePlayer.class))))
    @GetMapping("/api/players")
    public Object findPlayers(@RequestParam String playerName){

        int maxResults = 7;
        List<Player> players = playerRepository.findAllByName(playerName, maxResults);

        if(players.isEmpty())
            return new ResponseEntity<>("조건에 맞는 검색 결과가 없습니다", HttpStatus.CONFLICT);

        return players.stream().map(AutoCompletePlayer::new).toList();
    }


}
