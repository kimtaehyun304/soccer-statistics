package com.daelim.sfa.domain.team;

import com.daelim.sfa.domain.*;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints={@UniqueConstraint(name="team_statistics_league_id_team_id_season_unique", columnNames={"league_id", "team_id", "season"})})
public class TeamStatistics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_statistics_id")
    private Long id;

    @JoinColumn(name = "team_id")
    @OneToOne(fetch = FetchType.LAZY)
    private Team team;

    @JoinColumn(name = "league_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private League league;

    private int season;

    @Embedded
    private Fixtures fixtures;

    @Embedded
    private TeamStatisticsGoals goals;

    @Embedded
    private Cards cards;

    @OneToMany(mappedBy = "teamStatistics")
    List<Lineup> lineups = new ArrayList<>();

    @Builder
    public TeamStatistics(Long id, Team team, League league, Fixtures fixtures, TeamStatisticsGoals goals, Cards cards, int season) {
        this.id = id;
        this.team = team;
        this.league = league;
        this.fixtures = fixtures;
        this.goals = goals;
        this.cards = cards;
        this.season = season;
    }

    public void updateTeamStatistics(Fixtures fixtures, TeamStatisticsGoals goals, Cards cards) {
        this.fixtures = fixtures;
        this.goals = goals;
        this.cards = cards;
    }
}
