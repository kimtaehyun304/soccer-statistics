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
public class Dribbles {

    @Column(name = "dribbles_attempts")
    private int attempts;

    @Column(name = "dribbles_success")
    private int success;

    //private int past;

    @Builder
    public Dribbles(int attempts, int success) {
        this.attempts = attempts;
        this.success = success;
    }
}
