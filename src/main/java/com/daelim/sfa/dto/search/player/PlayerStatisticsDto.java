package com.daelim.sfa.dto.search.player;

import com.daelim.sfa.domain.League;
import com.daelim.sfa.domain.player.Dribbles;
import com.daelim.sfa.domain.player.Passes;
import com.daelim.sfa.domain.player.PlayerStatistics;
import jakarta.persistence.Column;
import lombok.Getter;

import java.util.List;
@Getter
public class PlayerStatisticsDto {

    private String leagueName;

    private int leagueSeason;

    private String position;

    //private Double rating = 0d;

    // passes, shots, assists, saves
    private GraphDto graph = new GraphDto();

    // 기타 정보
    private int goals;

    private int goalsConceded;

    private int tackles;

    private int dribbles;

    private int foulDrawn;

    private int foulCommitted;

    private int yellowCard;

    private int redCard;

    // 이적 선수는 통계를 여러개 가질 수 있기 때문이다.
    public PlayerStatisticsDto(List<PlayerStatistics> playerStatisticsList){

        for (PlayerStatistics playerStatistics : playerStatisticsList) {
            leagueName = playerStatistics.getLeague().getName();
            leagueSeason = playerStatistics.getSeason();
            position = playerStatistics.getPosition();
            //rating += playerStatistics.getRating();
            graph.addGraph(playerStatistics.getPasses().getTotal(), playerStatistics.getShots().getTotal(), playerStatistics.getGoals().getAssists(), playerStatistics.getGoals().getSaves());
            goals += playerStatistics.getGoals().getTotal();
            goalsConceded += playerStatistics.getGoals().getConceded();
            tackles += playerStatistics.getTackles();
            dribbles += playerStatistics.getDribbles().getAttempts();
            foulDrawn += playerStatistics.getFouls().getDrawn();
            foulCommitted += playerStatistics.getFouls().getCommitted();
            yellowCard += playerStatistics.getCards().getYellowTotal();
            redCard += playerStatistics.getCards().getRedTotal();
        }


        //rating = rating/playerStatisticsList.size();

    }

}
