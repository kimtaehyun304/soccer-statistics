package com.daelim.sfa.domain.player;

import com.daelim.sfa.domain.team.Team;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class PlayerTransfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "player_transfer_id")
    private Long id;

    @JoinColumn(name = "player_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Player player;

    private LocalDate date;

    private String type;

    @JoinColumn(name = "in_team_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Team inTeam;

    @JoinColumn(name = "out_team_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Team outTeam;

    // RAPID API 스펙에 있는 데이터 입니다
    private LocalDateTime updatedAt;

    @Builder
    public PlayerTransfer(Player player, LocalDate date, String type, Team inTeam, Team outTeam, LocalDateTime updatedAt) {
        this.player = player;
        this.date = date;
        this.type = type;
        this.inTeam = inTeam;
        this.outTeam = outTeam;
        this.updatedAt = updatedAt;
    }
}
