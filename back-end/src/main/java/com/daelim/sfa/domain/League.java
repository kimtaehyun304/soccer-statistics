package com.daelim.sfa.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class League {

    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "league_id")
    private Long id;

    @Column(unique = true)
    private String name;

    private String country;

    public League(Long id) {
        this.id = id;
    }
}
