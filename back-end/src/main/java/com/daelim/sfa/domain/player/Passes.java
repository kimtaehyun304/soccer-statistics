package com.daelim.sfa.domain.player;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Passes {

    @Column(name = "passes_total")
    private int total;

    @Column(name = "passes_key")
    private int key;

    @Column(name = "passes_accuracy")
    private int accuracy;

}
