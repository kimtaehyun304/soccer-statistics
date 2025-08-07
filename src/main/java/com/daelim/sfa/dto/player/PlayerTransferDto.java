package com.daelim.sfa.dto.player;

import com.daelim.sfa.domain.player.PlayerTransfer;
import com.daelim.sfa.dto.team.TeamDto;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class PlayerTransferDto {

    private LocalDate date;

    private String type;

    private TeamDto inTeam;

    private TeamDto outTeam;

    public PlayerTransferDto(PlayerTransfer playerTransfer){
        date = playerTransfer.getDate();
        type = playerTransfer.getType();
        inTeam = new TeamDto(playerTransfer.getInTeam());
        outTeam = new TeamDto(playerTransfer.getOutTeam());
    }
}
