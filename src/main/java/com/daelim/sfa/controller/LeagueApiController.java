package com.daelim.sfa.controller;

import com.daelim.sfa.domain.League;
import com.daelim.sfa.dto.LeagueDto;
import com.daelim.sfa.repository.LeagueRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class LeagueApiController {

    private final LeagueRepository leagueRepository;

    @Operation(summary = "리그 id-name 조회", description = "모든 리그 정보를 가져옵니다")
    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(mediaType = "application/json" , schema = @Schema(implementation = LeagueDto.class)))
    @GetMapping("/api/leagues")
    @ResponseBody
    public List<LeagueDto> findLeagues() {

        List<League> leagues = leagueRepository.findAll();
        return leagues.stream().map(LeagueDto::new).toList();
    }


}
