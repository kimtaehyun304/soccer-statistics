package com.daelim.sfa.repository.player;

import com.daelim.sfa.domain.player.PlayerStatistics;
import com.daelim.sfa.domain.team.TeamStatistics;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PlayerStatisticsRepository {

    private final EntityManager em;

    public void save(PlayerStatistics playerStatistics) {
        em.persist(playerStatistics);
    }

    public PlayerStatistics findById(Long id) {
        return em.find(PlayerStatistics.class, id);
    }

    public List<PlayerStatistics> findAll(){
        return em.createQuery("select p from PlayerStatistics p", PlayerStatistics.class).getResultList();
    }

    public List<PlayerStatistics> findAllByPlayerId(Long playerId){
        return em.createQuery("select p from PlayerStatistics p where p.player.id = :playerId", PlayerStatistics.class)
                .setParameter("playerId", playerId)
                .getResultList();
    }

    public List<PlayerStatistics> findAllWithLeagueByPlayerIdAndLeagueNameAndSeason(Long playerId, String leagueName, int season){
        return em.createQuery("select ps from PlayerStatistics ps join fetch ps.league l " +
                        "where ps.player.id = :playerId and l.name =: leagueName and ps.season = :season", PlayerStatistics.class)
                .setParameter("playerId", playerId)
                .setParameter("leagueName", leagueName)
                .setParameter("season", season)
                .getResultList();
    }

    public List<PlayerStatistics> findAllWithLeagueByLeagueIdAndSeason(Long leagueId, int season){
        return em.createQuery("select ps from PlayerStatistics ps join fetch ps.league l " +
                        "where ps.league.id =: leagueId and ps.season = :season", PlayerStatistics.class)
                .setParameter("leagueId", leagueId)
                .setParameter("season", season)
                .getResultList();
    }

    public List<PlayerStatistics> findAllWithLeagueByPlayerIdAndSeason(Long playerId, int season){
        return em.createQuery("select ps from PlayerStatistics ps join fetch ps.league l " +
                        "where ps.player.id = :playerId and ps.season = :season", PlayerStatistics.class)
                .setParameter("playerId", playerId)
                .setParameter("season", season)
                .getResultList();
    }


}
