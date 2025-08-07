package com.daelim.sfa.dto.ranking;

import lombok.Getter;

@Getter
public class TeamRankingDto {

    private int ranking;

    private Long id;

    private String logo;

    private String name;

    private int goals;

    private int against;

    private int played;

    private int wins;

    private int losses;

    private int draws;

    //private String winRate;

    private int points;

    public TeamRankingDto(Long id, String logo, String name, int goals, int against, int played, int wins, int losses, int draws, int points) {
        this.id = id;
        this.logo = logo;
        this.name = name;
        this.goals = goals;
        this.against = against;
        this.played = played;
        this.wins = wins;
        this.losses = losses;
        this.draws = draws;
        //이기면 3점 비기면 1점
        this.points = points;
    }

    public void addRanking(int i){
        ranking = i;
    }

}
