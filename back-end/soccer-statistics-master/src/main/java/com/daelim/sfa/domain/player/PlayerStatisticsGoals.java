package com.daelim.sfa.domain.player;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlayerStatisticsGoals {

    @Column(name = "goals_total")
    private int total;

    @Column(name = "goals_conceded")
    private int conceded;

    @Column(name = "goals_assists")
    private int assists;

    @Column(name = "goals_saves")
    private int saves;

    @Builder
    public PlayerStatisticsGoals(int total, int conceded, int assists, int saves) {
        this.total = total;
        this.conceded = conceded;
        this.assists = assists;
        this.saves = saves;
    }
}
