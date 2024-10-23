package com.daelim.sfa.dto.search.team;

import com.daelim.sfa.domain.team.Lineup;
import lombok.Getter;

@Getter
public class LineupDto {

    private String formation;

    private int played;

    public LineupDto(Lineup lineup) {
        this.formation = lineup.getFormation();
        this.played = lineup.getPlayed();
    }
}
