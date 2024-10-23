package com.daelim.sfa.dto.ranking;

import lombok.Getter;

@Getter
public class PlayerRankingDto {

    private int ranking;

    private String photo;

    private String name;

    private String position;

    private Double rating;

    public PlayerRankingDto(String photo, String name, String position, Double rating) {
        this.photo = photo;
        this.name = name;
        this.position = position;
        this.rating = rating;
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
