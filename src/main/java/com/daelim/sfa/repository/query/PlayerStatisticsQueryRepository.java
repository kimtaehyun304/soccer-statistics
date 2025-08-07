package com.daelim.sfa.repository.query;


import com.daelim.sfa.dto.ranking.PlayerRankingDto;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PlayerStatisticsQueryRepository {

    private final EntityManager em;

    // 그래프 토탈로 순위를 정함
    /*
    public List<PlayerRankingDto> findAllByLeagueNameAndLeagueSeason(String leagueName, int leagueSeason){
        List<PlayerRankingDto> PlayerRankings;

        if (leagueName == null){
            PlayerRankings = em.createQuery("select new com.daelim.sfa.dto.ranking.PlayerRankingDto(p.player.photo ,CONCAT(p.player.firstName, ' ', p.player.lastName), p.position, (p.passes.total+p.shots.total+p.goals.assists+p.goals.saves)) " +
                            "from PlayerStatistics p join p.player " +
                            "where p.season =:leagueSeason " +
                            "order by (p.passes.total+p.shots.total+p.goals.assists+p.goals.saves) desc", PlayerRankingDto.class)
                    .setParameter("leagueSeason", leagueSeason)
                    .setMaxResults(100)
                    .getResultList();
        }else {
            PlayerRankings = em.createQuery("select new com.daelim.sfa.dto.ranking.PlayerRankingDto(p.player.photo, CONCAT(p.player.firstName, ' ', p.player.lastName), p.position, (p.passes.total+p.shots.total+p.goals.assists+p.goals.saves)) " +
                            "from PlayerStatistics p join p.player " +
                            "where p.league.name = :leagueName and p.season =:leagueSeason " +
                            "order by (p.passes.total+p.shots.total+p.goals.assists+p.goals.saves) desc", PlayerRankingDto.class)
                    .setParameter("leagueName", leagueName)
                    .setParameter("leagueSeason", leagueSeason)
                    .setMaxResults(100)
                    .getResultList();
        }

        int i = 1;
        for (PlayerRankingDto playerRanking : PlayerRankings) {
            playerRanking.addRanking(i);
            i++;
        }

        return PlayerRankings;
    }
    */

    // rating으로 순위를 정함
    public List<PlayerRankingDto> findAllByLeagueIdAndLeagueSeason(Long leagueId, int leagueSeason){
        List<PlayerRankingDto> PlayerRankings;

        if (leagueId == null){
            PlayerRankings = em.createQuery("select distinct new com.daelim.sfa.dto.ranking.PlayerRankingDto(p.id, p.photo ,CONCAT(p.firstName, ' ', p.lastName), ps.position, CAST(ROUND(ps.rating, 2) AS DOUBLE)) " +
                            "from PlayerStatistics ps join ps.player p " +
                            "where ps.season =:leagueSeason " +
                            "order by (ps.rating) desc", PlayerRankingDto.class)
                    .setParameter("leagueSeason", leagueSeason)
                    .setMaxResults(100)
                    .getResultList();
        }else {
            PlayerRankings = em.createQuery("select distinct new com.daelim.sfa.dto.ranking.PlayerRankingDto(p.id, p.photo ,CONCAT(p.firstName, ' ', p.lastName), ps.position, CAST(ROUND(ps.rating, 2) AS DOUBLE)) " +
                            "from PlayerStatistics ps join ps.player p " +
                            "where ps.league.id = :leagueId and ps.season =:leagueSeason " +
                            "order by (ps.rating) desc", PlayerRankingDto.class)
                    .setParameter("leagueId", leagueId)
                    .setParameter("leagueSeason", leagueSeason)
                    .setMaxResults(100)
                    .getResultList();
        }

        int i = 1;
        for (PlayerRankingDto playerRanking : PlayerRankings) {
            playerRanking.addRanking(i);
            i++;
        }

        return PlayerRankings;
    }

}
