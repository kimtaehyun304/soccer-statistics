package com.daelim.sfa.controller;

import com.daelim.sfa.auth.JwtAuth;
import com.daelim.sfa.domain.Member;
import com.daelim.sfa.domain.player.Player;
import com.daelim.sfa.domain.player.PlayerComment;
import com.daelim.sfa.domain.team.Team;
import com.daelim.sfa.domain.team.TeamComment;
import com.daelim.sfa.dto.comment.RequestCommentDto;
import com.daelim.sfa.dto.comment.ResponseCommentDto;
import com.daelim.sfa.dto.comment.ResponseCountAndCommentDto;
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
public class CommentApiController {

    private final PlayerRepository playerRepository;
    private final PlayerCommentRepository playerCommentRepository;
    private final PlayerCommentService playerCommentService;

    private final TeamRepository teamRepository;
    private final TeamCommentRepository teamCommentRepository;
    private final TeamCommentService teamCommentService;

    private final MemberRepository  memberRepository;
    private final JwtAuth jwtAuth;

    @Operation(summary = "특정 선수 댓글 조회", description = "선수 PK 로 검색합니다")
    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(mediaType = "application/json" , schema = @Schema(implementation = ResponseCountAndCommentDto.class)))
    @GetMapping("/api/players/{playerId}/comments")
    @ResponseBody
    public Object findCommentByPlayerId(@PathVariable Long playerId, @RequestParam(required = false, defaultValue = "1") int page) {

        Player player = playerRepository.findById(playerId);
        int maxResults = 10;
        List<PlayerComment> playerComments = playerCommentRepository.findAllWithMemberByPlayerId(player.getId(), page, maxResults);
        int count = Math.toIntExact(playerCommentRepository.countByPlayerIdAndParentIdIsNull(player.getId()));
        List<ResponseCommentDto> commentDtos = playerComments.stream().map(ResponseCommentDto::new).toList();
        return new ResponseCountAndCommentDto((int) Math.ceil((double) count/maxResults), commentDtos);
    }

    @Operation(summary = "특정 선수 댓글 저장", description = "인증 헤더에 jwt를 넣어주세요. 리플을 저장하는 경우 부모 댓글 ID도 함께 넣어주세요")
    @ApiResponse(responseCode = "200", description = "저장 성공", content = @Content(mediaType = "application/json" , schema = @Schema(implementation = ResponseCommentDto.class)))
    @PostMapping("/api/players/{playerId}/comments")
    @ResponseBody
    public ResponseEntity<Object> saveComment(@PathVariable Long playerId, @RequestBody @Valid RequestCommentDto requestCommentDto, BindingResult bindingResult, HttpServletRequest request) {

        String jws = request.getHeader("Authorization").replace("Bearer ", "");

        if (jws.isEmpty())
            return new ResponseEntity<>("로그인 해주세요", HttpStatus.UNAUTHORIZED);

        Long memberId = jwtAuth.getMemberIdBySubject(jws);

        if (memberId == -1L)
            return new ResponseEntity<>("로그인 만료 다시 로그인 해주세요", HttpStatus.CONFLICT);

        if(bindingResult.hasErrors()) {
            StringBuilder message = new StringBuilder();
            for (FieldError fieldError : bindingResult.getFieldErrors())
                message.append(fieldError.getField()).append("는(은) ").append(fieldError.getDefaultMessage()).append(". ");
            message.deleteCharAt(message.length()-1);
            return new ResponseEntity<>(message.toString(), HttpStatus.CONFLICT);
        }

        Player foundPlayer = playerRepository.findById(playerId);
        //Player player = new Player(foundPlayer.getId());
        PlayerComment parent = requestCommentDto.getParentId() == null ? null : new PlayerComment(requestCommentDto.getParentId());
        Member member = memberRepository.findById(memberId);
        PlayerComment comment = PlayerComment.builder().player(foundPlayer).parent(parent).member(member).content(requestCommentDto.getContent()).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();
        playerCommentService.saveComment(comment);

        ResponseCommentDto responseCommentDto = new ResponseCommentDto(comment);

        return new ResponseEntity<>(responseCommentDto, HttpStatus.CREATED);
    }

    @Operation(summary = "특정 팀 댓글 조회", description = "팀 PK 로 검색합니다 ")
    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(mediaType = "application/json" , schema = @Schema(implementation = ResponseCountAndCommentDto.class)))
    @GetMapping("/api/teams/{teamId}/comments")
    @ResponseBody
    public Object findCommentByTeamId(@PathVariable Long teamId, @RequestParam(required = false, defaultValue = "1") int page) {

        Team team = teamRepository.findById(teamId);
        int maxResults = 10;

        List<TeamComment> teamComments = teamCommentRepository.findAllWithMemberByTeamId(team.getId(), page, maxResults);
        int count = Math.toIntExact(teamCommentRepository.countByTeamIdAndParentId(team.getId()));
        List<ResponseCommentDto> commentDtos = teamComments.stream().map(ResponseCommentDto::new).toList();
        return new ResponseCountAndCommentDto((int) Math.ceil((double) count/maxResults), commentDtos);
    }

    @Operation(summary = "특정 팀 댓글 저장", description = "인증 헤더에 jwt를 넣어주세요. 리플을 저장하는 경우 부모 댓글 ID도 함께 넣어주세요")
    @ApiResponse(responseCode = "200", description = "저장 성공", content = @Content(mediaType = "application/json" , schema = @Schema(implementation = ResponseCommentDto.class)))
    @PostMapping("/api/teams/{teamId}/comments")
    @ResponseBody
    public ResponseEntity<Object> saveTeamComment(@PathVariable Long teamId, @RequestBody @Valid RequestCommentDto requestCommentDto, BindingResult bindingResult, HttpServletRequest request) {

        String jws = request.getHeader("Authorization").replace("Bearer ", "");;

        if (jws.isEmpty())
            return new ResponseEntity<>("로그인 해주세요", HttpStatus.UNAUTHORIZED);

        Long memberId = jwtAuth.getMemberIdBySubject(jws);

        if (memberId == -1L)
            return new ResponseEntity<>("로그인 만료 다시 로그인 해주세요", HttpStatus.CONFLICT);

        if(bindingResult.hasErrors()) {
            StringBuilder message = new StringBuilder();
            for (FieldError fieldError : bindingResult.getFieldErrors())
                message.append(fieldError.getField()).append("는(은) ").append(fieldError.getDefaultMessage()).append(". ");
            message.deleteCharAt(message.length()-1);
            return new ResponseEntity<>(message.toString(), HttpStatus.CONFLICT);
        }

        Team foundTeam = teamRepository.findById(teamId);
        TeamComment parent = requestCommentDto.getParentId() == null ? null : new TeamComment(requestCommentDto.getParentId());
        Member member = memberRepository.findById(memberId);
        TeamComment comment = TeamComment.builder().team(foundTeam).parent(parent).member(member).content(requestCommentDto.getContent()).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();
        teamCommentService.saveComment(comment);

        ResponseCommentDto responseCommentDto = new ResponseCommentDto(comment);

        return new ResponseEntity<>(responseCommentDto, HttpStatus.CREATED);
    }

}
