package com.daelim.sfa.dto.search.team;

import com.daelim.sfa.domain.team.Team;
import lombok.Getter;

@Getter
public class TeamInformationDto {

    private String teamName;

    private String country;

    private int founded;

    private boolean national;

    private String logo;

    public TeamInformationDto(Team team) {
        teamName = team.getName();
        country = team.getCountry();
        founded = team.getFounded();
        national = team.isNational();
        logo = team.getLogo();
    }
}
