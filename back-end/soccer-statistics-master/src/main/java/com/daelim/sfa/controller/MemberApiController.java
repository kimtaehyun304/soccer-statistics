package com.daelim.sfa.controller;

import com.daelim.sfa.auth.JwtAuth;
import com.daelim.sfa.domain.Member;
import com.daelim.sfa.dto.JwtTokenDto;
import com.daelim.sfa.dto.SignInDto;
import com.daelim.sfa.dto.SignUpDto;
import com.daelim.sfa.repository.MemberRepository;
import com.daelim.sfa.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final MemberRepository memberRepository;
    private final JwtAuth jwtAuth;

    @PostMapping("/api/members/signup")
    @Operation(summary = "회원가입", description = "DB에 회원 정보를 저장합니다")
    public ResponseEntity<String> doSignUp(@Valid @RequestBody SignUpDto signUpDto, BindingResult bindingResult){

        /*
        if(signUpDto == null)
            return new ResponseEntity<>("loginId, password, name이 누락됐습니다", HttpStatus.CONFLICT);
        */

        if(bindingResult.hasErrors()) {
            StringBuilder message = new StringBuilder();
            for (FieldError fieldError : bindingResult.getFieldErrors())
                message.append(fieldError.getField()).append("는(은) ").append(fieldError.getDefaultMessage()).append(". ");
            message.deleteCharAt(message.length()-1);
            return new ResponseEntity<>(message.toString(), HttpStatus.CONFLICT);
        }

        String encodedPassword = bCryptPasswordEncoder.encode(signUpDto.getPassword());
        Member member = Member.builder().loginId(signUpDto.getLoginId()).password(encodedPassword).name(signUpDto.getName()).createdAt(LocalDateTime.now()).build();
        Long memberId = memberService.save(member);

        if(memberId == null)
            return new ResponseEntity<>("이미 사용중인 로그인 ID 입니다", HttpStatus.CONFLICT);
        else
            return new ResponseEntity<>("회원가입 성공", HttpStatus.OK);
    }

    @PostMapping("/api/members/signin")
    @Operation(summary = "로그인", description = "로그인을 성공하면 accessToken을 반환합니다")
    @ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content(mediaType = "application/json" , schema = @Schema(implementation = JwtTokenDto.class)))
    public Object doSignIn(@Valid @RequestBody SignInDto signInDto, BindingResult bindingResult){

        /*
        if(signInDto == null)
            return new ResponseEntity<>("loginId, password가 누락됐습니다", HttpStatus.CONFLICT);
        */

        if(bindingResult.hasErrors()) {
            StringBuilder message = new StringBuilder();
            for (FieldError fieldError : bindingResult.getFieldErrors())
                message.append(fieldError.getField()).append("는(은) ").append(fieldError.getDefaultMessage()).append(". ");
            message.deleteCharAt(message.length()-1);
            return new ResponseEntity<>(message.toString(), HttpStatus.CONFLICT);
        }

        Member foundMember = memberRepository.findByLoginId(signInDto.getLoginId());

        if(foundMember == null)
            return new ResponseEntity<>("로그인 실패", HttpStatus.CONFLICT);

        String foundEncryptedPassword = foundMember.getPassword();

        // JWT 발급
        if(bCryptPasswordEncoder.matches(signInDto.getPassword(), foundEncryptedPassword)){
            String accessToken = jwtAuth.createAccessToken(foundMember.getId());
            //String refreshToken = jwtAuth.createRefreshToken(foundMember.getId());
            return JwtTokenDto.builder().accessToken(accessToken).build();
        }else
            return new ResponseEntity<>("로그인 실패", HttpStatus.CONFLICT);


    }


}
