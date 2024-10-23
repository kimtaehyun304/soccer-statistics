package com.daelim.sfa.domain.player;

import com.daelim.sfa.domain.*;
import com.daelim.sfa.domain.team.Cards;
import com.daelim.sfa.domain.team.Team;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlayerStatistics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "player_statistics_id")
    private Long id;

    @JoinColumn(name = "player_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Player player;

    @JoinColumn(name = "team_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Team team;

    @JoinColumn(name = "league_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private League league;

    private int season;

    //@Enumerated(EnumType.STRING)
    private String position;

    private Double rating;

    //private boolean captin;

    @Embedded
    private Shots shots;

    @Embedded
    private PlayerStatisticsGoals goals;

    @Embedded
    private Passes passes;

    private int tackles;

    @Embedded
    private Dribbles dribbles;

    @Embedded
    private Fouls fouls;

    @Embedded
    private Cards cards;

    @Builder
    public PlayerStatistics(Player player, Team team, League league, int season, String position, Double rating, Shots shots, PlayerStatisticsGoals goals, Passes passes, int tackles, Dribbles dribbles, Fouls fouls, Cards cards) {
        this.player = player;
        this.team = team;
        this.league = league;
        this.season = season;
        this.position = position;
        this.rating = rating;
        this.shots = shots;
        this.goals = goals;
        this.passes = passes;
        this.tackles = tackles;
        this.dribbles = dribbles;
        this.fouls = fouls;
        this.cards = cards;
    }

    public void updatePlayerStatistics(Double rating, Shots shots, PlayerStatisticsGoals goals, Passes passes, int tackles, Dribbles dribbles, Fouls fouls, Cards cards) {
        this.rating = rating;
        this.shots = shots;
        this.goals = goals;
        this.passes = passes;
        this.tackles = tackles;
        this.dribbles = dribbles;
        this.fouls = fouls;
        this.cards = cards;
    }

}
