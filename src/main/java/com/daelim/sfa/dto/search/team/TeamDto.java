package com.daelim.sfa.dto.search.team;

import com.daelim.sfa.domain.game.GameFixture;
import com.daelim.sfa.domain.team.Team;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import lombok.Getter;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Getter
@JsonInclude(NON_NULL)
public class TeamDto {

    private String name;

    private String logo;

    private int goals;

    public TeamDto(Team team, int goals) {
        name = team.getName();
        logo = team.getLogo();
        this.goals = goals;
    }

    public TeamDto(Team team) {
        name = team.getName();
        logo = team.getLogo();
    }
}
