package com.daelim.sfa.domain.team;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Venue {

    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "venue_id")
    private Long id;

    private String name;

    private String address;

    private String city;

    private int capacity;

    private String surface;

    private String image;

    public Venue(Long id) {
        this.id = id;
    }
}
