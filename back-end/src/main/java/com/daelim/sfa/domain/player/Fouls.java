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
public class Fouls {

    @Column(name = "fouls_drawn")
    private int drawn;

    @Column(name = "fouls_committed")
    private int committed;

    @Builder
    public Fouls(int drawn, int committed) {
        this.drawn = drawn;
        this.committed = committed;
    }
}
