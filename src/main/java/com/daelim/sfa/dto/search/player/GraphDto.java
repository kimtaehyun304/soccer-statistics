package com.daelim.sfa.dto.search.player;

import lombok.Getter;

//  사각형 그래프
@Getter
public class GraphDto {

    private int passes;

    private int shots;

    private int assists;

    private int saves;

    public void addGraph(int passes, int shots, int assists, int saves){
        this.passes += passes;
        this.shots += shots;
        this.assists += assists;
        this.saves += saves;
    }

}
