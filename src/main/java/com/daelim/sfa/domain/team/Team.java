package com.daelim.sfa.domain.team;

import com.daelim.sfa.domain.player.Player;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints={@UniqueConstraint(name="name_code_founded_unique", columnNames={"name", "code", "founded"})})
public class Team {

    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id")
    private Long id;

    private String name;

    private String code;

    private String country;

    private int founded;

    private boolean national;

    private String logo;

    @JoinColumn(name = "venue_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Venue venue;

    @OneToMany(mappedBy = "team")
    private List<Player> players = new ArrayList<>();

    public void addVenue(Venue venue) {
        this.venue = venue;
    }

    public Team(Long id) {
        this.id = id;
    }
}
