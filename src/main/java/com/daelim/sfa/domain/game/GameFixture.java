package com.daelim.sfa.domain.game;

import com.daelim.sfa.domain.League;
import com.daelim.sfa.domain.team.Team;
import com.daelim.sfa.domain.team.Venue;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameFixture {

    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "game_fixture_id")
    private Long id;

    // 심판
    private String referee;

    // UTC 등
    private String timezone;

    private LocalDateTime date;

    @JoinColumn(name = "venue_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Venue venue;

    @JoinColumn(name = "home_team_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Team homeTeam;

    @JoinColumn(name = "league_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private League league;

    private int season;

    @JoinColumn(name = "team1_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Team team1;

    @JoinColumn(name = "team2_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Team team2;

    @Column(name = "team1_goals")
    private Integer team1Goals;

    @Column(name = "team2_goals")
    private Integer team2Goals;

    @JoinColumn(name = "winner_team_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Team winnerTeam;

    /*
    @Embedded
    @OneToMany
    List<GameFixturesDetail> details = new ArrayList<>();
     */

    @Builder
    public GameFixture(Long id, String referee, String timezone, LocalDateTime date, Venue venue, Team homeTeam, League league, int season, Team team1, Team team2, Team winnerTeam, Integer team1Goals, Integer team2Goals) {
        this.id = id;
        this.referee = referee;
        this.timezone = timezone;
        this.date = date;
        this.venue = venue;
        this.homeTeam = homeTeam;
        this.league = league;
        this.season = season;
        this.team1 = team1;
        this.team2 = team2;
        this.winnerTeam = winnerTeam;
        this.team1Goals = team1Goals;
        this.team2Goals = team2Goals;
    }

    public void updateFinishedGame(String referee, Team winnerTeam, Integer team1Goals, Integer team2Goals) {
        this.referee = referee;
        this.winnerTeam = winnerTeam;
        this.team1Goals = team1Goals;
        this.team2Goals = team2Goals;
    }
}
