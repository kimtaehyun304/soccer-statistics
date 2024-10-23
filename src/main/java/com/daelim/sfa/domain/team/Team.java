package com.daelim.sfa.domain.team;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Team {

    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id")
    private Long id;

    @Column(unique = true)
    private String name;

    @Column(unique = true)
    private String code;

    private String country;

    private int founded;

    private boolean national;

    private String logo;

    @JoinColumn(name = "venue_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Venue venue;

    public void addVenue(Venue venue) {
        this.venue = venue;
    }

    public Team(Long id) {
        this.id = id;
    }
}
