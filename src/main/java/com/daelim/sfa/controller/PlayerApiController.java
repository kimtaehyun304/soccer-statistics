package com.daelim.sfa.controller;

import com.daelim.sfa.domain.player.Player;
import com.daelim.sfa.domain.player.PlayerStatistics;
import com.daelim.sfa.domain.player.PlayerTransfer;
import com.daelim.sfa.dto.LeagueIdSeasonDto;
import com.daelim.sfa.dto.player.*;
import com.daelim.sfa.repository.PlayerTransferRepository;
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

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class PlayerApiController {

    private final PlayerRepository playerRepository;
    private final PlayerStatisticsRepository playerStatisticsRepository;
    private final PlayerTransferRepository playerTransferRepository;

    @Operation(summary = "한 선수의 정보와 통계 조회", description = "선수 PK 로 검색합니다")
    @Parameter(name = "playerId", description = "", example = "186")
    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(mediaType = "application/json" , schema = @Schema(implementation = SearchPlayerDto.class)))
    @GetMapping("/api/players/{playerId}")
    public Object findPlayer(@PathVariable Long playerId, @ModelAttribute LeagueIdSeasonDto leagueIdSeasonDto){
        Player player = playerRepository.findById(playerId);

        if(player == null)
            return new ResponseEntity<>("조건에 맞는 검색 결과가 없습니다", HttpStatus.CONFLICT);

        List<PlayerTransfer> playerTransfers = playerTransferRepository.findAllByPlayerId(player.getId());

        // 한번에 업데이트 하는거라 시간 다 똑같습니다.
        LocalDateTime updatedAt = null;
        if(!playerTransfers.isEmpty())
            updatedAt = playerTransfers.get(0).getUpdatedAt();
        List<PlayerTransferDto> playerTransferDtos = playerTransfers.stream().map(PlayerTransferDto::new).toList();
        TransferResult<List<PlayerTransferDto>> genericPlayerTransferDtos = new TransferResult<>(updatedAt, playerTransferDtos);

        // 포메이션 별 슛 패스 순위 어떻게 넣지
        List<PlayerStatistics> playerStatisticsList = playerStatisticsRepository.findAllWithLeagueByPlayerIdAndSeason(player.getId(), leagueIdSeasonDto.getLeagueSeason());
        SearchPlayerDto searchPlayerDto = new SearchPlayerDto(player, playerStatisticsList, genericPlayerTransferDtos);

        for (PlayerStatisticsDto statistics : searchPlayerDto.getStatisticsList()) {
            Long leagueId = statistics.getLeagueId();
            int season = statistics.getLeagueSeason();
            String position = statistics.getPosition();
            StatRankingDto statRankingDto = playerStatisticsRepository.findStatRankingByLeagueIdAndSeasonPlayerIdAndPosition(leagueId, season, playerId, position);
            statistics.addStatRanking(statRankingDto.getPassesRanking(), statRankingDto.getShotsRanking(), statRankingDto.getAssistsRanking(), statRankingDto.getSavesRanking(), statRankingDto.getGoalsRanking());
        }

        return searchPlayerDto;
    }

    @Operation(summary = "선수 리스트 조회", description = "연관된 선수를 조회합니다.")
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
