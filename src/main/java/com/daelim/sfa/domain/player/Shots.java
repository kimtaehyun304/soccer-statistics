package com.daelim.sfa.domain.player;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Shots {

    @Column(name = "shots_total")
    private int total;

    @Column(name = "shots_on")
    private int on;

    @Builder
    public Shots(int total, int on) {
        this.total = total;
        this.on = on;
    }
}
