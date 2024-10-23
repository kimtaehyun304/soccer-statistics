package com.daelim.sfa.domain.team;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// TeamStatistics PlayerStatistics 공용
public class Cards {

    private int yellowTotal;

    private int redTotal;

    @Builder
    public Cards(int yellowTotal, int redTotal) {
        this.yellowTotal = yellowTotal;
        this.redTotal = redTotal;
    }
}
