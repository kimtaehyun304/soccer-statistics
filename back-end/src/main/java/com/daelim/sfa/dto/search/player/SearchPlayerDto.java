package com.daelim.sfa.dto.search.player;

import com.daelim.sfa.domain.player.Player;
import com.daelim.sfa.domain.player.PlayerStatistics;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.parameters.P;

import java.util.List;

@Getter
public class SearchPlayerDto {

    private PlayerInformationDto information;

    private PlayerStatisticsDto statistics;

    public SearchPlayerDto(Player player, List<PlayerStatistics> playerStatisticsList){
        information = new PlayerInformationDto(player);
        statistics = new PlayerStatisticsDto(playerStatisticsList);
    }

}
