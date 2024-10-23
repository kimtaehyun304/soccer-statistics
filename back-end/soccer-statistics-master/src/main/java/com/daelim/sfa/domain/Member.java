package com.daelim.sfa.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private String loginId;

    private String password;

    private String name;

    private LocalDateTime createdAt;

    @Builder
    public Member(String loginId, String password, String name, LocalDateTime createdAt) {
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.createdAt = createdAt;
    }

    public Member(Long id) {
        this.id = id;
    }
}
