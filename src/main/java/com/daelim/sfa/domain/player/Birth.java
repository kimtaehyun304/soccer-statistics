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
public class Birth {

    @Column(name = "birth_date")
    private LocalDate date;

    //private String place;

    @Column(name = "birth_country")
    private String country;

}
