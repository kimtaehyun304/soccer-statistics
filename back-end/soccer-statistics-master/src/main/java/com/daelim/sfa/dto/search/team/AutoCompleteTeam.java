package com.daelim.sfa.dto.search.team;

import com.daelim.sfa.domain.team.Team;
import lombok.Getter;

@Getter
public class AutoCompleteTeam {

    private String logo;

    private String name;

    public AutoCompleteTeam(Team team) {
        logo = team.getLogo();
        name = team.getName();
    }
}
