package com.daelim.sfa.dto.player;

import lombok.Getter;
import lombok.ToString;

//  사각형 그래프
@Getter
@ToString
public class StatRankingDto {

    private int goalsRanking;
    private int passesRanking;
    private int shotsRanking;
    private int savesRanking;
    private int assistsRanking;

    public StatRankingDto(int goalsRanking, int passesRanking, int shotsRanking, int savesRanking, int assistsRanking) {
        this.goalsRanking = goalsRanking;
        this.passesRanking = passesRanking;
        this.shotsRanking = shotsRanking;
        this.savesRanking = savesRanking;
        this.assistsRanking = assistsRanking;
    }
}
