package com.daelim.sfa.dto.ranking;

import lombok.Getter;

@Getter
public class PlayerRankingDto {

    private int ranking;

    private Long playerId;

    private String photo;

    private String name;

    private String position;

    private Double rating;

    public PlayerRankingDto(Long playerId, String photo, String name, String position, Double rating) {
        this.playerId = playerId;
        this.photo = photo;
        this.name = name;
        this.position = position;
        this.rating = rating;
    }

    public PlayerRankingDto(Long playerId) {
        this.playerId = playerId;
    }

    /*
    private int graphTotal;

    public PlayerRankingDto(String photo, String name, String position, int graphTotal) {
        this.photo = photo;
        this.name = name;
        this.position = position;
        this.graphTotal = graphTotal;
    }
     */
    public void addRanking(int i){
        ranking = i;
    }

}
