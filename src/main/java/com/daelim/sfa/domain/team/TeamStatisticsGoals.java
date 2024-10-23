package com.daelim.sfa.domain.team;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamStatisticsGoals {

    @Column(name = "goals_for_total")
    private int forTotal;

    @Column(name = "goals_against_total")
    private int againstTotal;

    @Builder
    public TeamStatisticsGoals(int forTotal, int againstTotal) {
        this.forTotal = forTotal;
        this.againstTotal = againstTotal;
    }

}
