package com.daelim.sfa.domain.team;

import com.daelim.sfa.domain.team.TeamStatistics;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Lineup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lineup_id")
    private Long id;

    @JoinColumn(name = "team_statistics_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private TeamStatistics teamStatistics;

    private String formation;

    private int played;

    public void addTeamStatistics(TeamStatistics teamStatistics){
        this.teamStatistics = teamStatistics;
    }

    public void updateLineup(int played){
        this.played = played;
    }
}
