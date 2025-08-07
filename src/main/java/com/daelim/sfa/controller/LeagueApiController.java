package com.daelim.sfa.controller;

import com.daelim.sfa.auth.JwtAuth;
import com.daelim.sfa.domain.League;
import com.daelim.sfa.domain.Member;
import com.daelim.sfa.domain.player.Player;
import com.daelim.sfa.domain.player.PlayerComment;
import com.daelim.sfa.domain.team.Team;
import com.daelim.sfa.domain.team.TeamComment;
import com.daelim.sfa.dto.LeagueDto;
import com.daelim.sfa.dto.comment.RequestCommentDto;
import com.daelim.sfa.dto.comment.ResponseCommentDto;
import com.daelim.sfa.dto.comment.ResponseCountAndCommentDto;
import com.daelim.sfa.repository.LeagueRepository;
import com.daelim.sfa.repository.MemberRepository;
import com.daelim.sfa.repository.player.PlayerCommentRepository;
import com.daelim.sfa.repository.player.PlayerRepository;
import com.daelim.sfa.repository.team.TeamCommentRepository;
import com.daelim.sfa.repository.team.TeamRepository;
import com.daelim.sfa.service.PlayerCommentService;
import com.daelim.sfa.service.TeamCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
        return leagues.stream().map(l -> new LeagueDto(l)).toList();
    }


}
