package com.daelim.sfa.domain.team;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// TeamStatistics Fixtures
public class Fixtures {

    private int played;

    private int wins;

    private int losses;

    private int draws;

    @Builder
    public Fixtures(int played, int wins, int losses, int draws) {
        this.played = played;
        this.wins = wins;
        this.losses = losses;
        this.draws = draws;
    }

}
