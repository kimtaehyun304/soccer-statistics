package com.daelim.sfa.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Component
@Slf4j
public class JwtAuth {
    private final SecretKey key;

    public JwtAuth(@Value("${jwt.secret}") String encodedKey){
        // BASE64 -> SecretKey
        key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(encodedKey));
    }

    public String createAccessToken(Long memberId){
        long expiration = 1000 * 60 * 60;
        //long expiration = 1000 * 60 * 60; // 60분
        return Jwts.builder()
                .subject(String.valueOf(memberId)) // 사용자 정보 (ex: username)
                //.issuedAt(new Date()) // 발급 시각
                .expiration(new Date(System.currentTimeMillis() + expiration)) // 만료 시각
                .signWith(key) // 서명
                .compact();
    }

    public String createRefreshToken(Long memberId){
        long expiration = 1000 * 60 * 60 * 24 * 14; // 2주일
        return Jwts.builder()
                .subject(String.valueOf(memberId)) // 사용자 정보 (ex: username)
                //.issuedAt(new Date()) // 발급 시각
                .expiration(new Date(new Date().getTime() + expiration)) // 만료 시각
                .signWith(key) // 서명
                .compact();
    }

    public Long getMemberIdBySubject(String jws){
        try {
            // 유효기간 만료 체크 포함
            Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(jws).getPayload();
            return Long.valueOf(claims.getSubject());
        }catch (JwtException e){
            //log.info("{}", e.getMessage());
            return -1L;
        }
    }

}
