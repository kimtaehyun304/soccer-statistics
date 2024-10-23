package com.daelim.sfa.repository.team;

import com.daelim.sfa.domain.team.TeamStatistics;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TeamStatisticsRepository {

    private final EntityManager em;

    public void save(TeamStatistics teamStatistics) {
        em.persist(teamStatistics);
    }

    public TeamStatistics findById(Long id) {
        return em.find(TeamStatistics.class, id);
    }
    /*
    public TeamStatistics findWithLeagueByTeamId(Long teamId) {
        return em.createQuery("select t from TeamStatistics t join fetch t.league where t.team.id = :teamId", TeamStatistics.class)
                .setParameter("teamId", teamId)
                .getResultList().get(0);
    }
     */

    public TeamStatistics findWithLeagueByTeamIdAndLeagueIdAndSeason(Long teamId, Long leagueId, int season) {
        return em.createQuery("select ts from TeamStatistics ts " +
                        "where ts.team.id = :teamId and ts.league.id =: leagueId and ts.season = :season", TeamStatistics.class)
                .setParameter("teamId", teamId)
                .setParameter("leagueId", leagueId)
                .setParameter("season", season)
                .getResultList().get(0);
    }

    public List<TeamStatistics> findAll(){
        return em.createQuery("select t from TeamStatistics t", TeamStatistics.class).getResultList();
    }

    /*
    public List<TeamStatistics> findAllWithLineUpByLeagueIdAndSeason(Long leagueId, int season){
        return em.createQuery("select ts from TeamStatistics ts join fetch ts.lineups " +
                "where ts.league.id = :leagueId and ts.season =: season", TeamStatistics.class)
                .setParameter("leagueId", leagueId)
                .setParameter("season", season)
                .getResultList();
    }
     */

    public List<TeamStatistics> findAllWithLineUpByLeagueIdAndSeasonInTeamId(Long leagueId, int season, List<Long> teamIds){
        return em.createQuery("select ts from TeamStatistics ts join fetch ts.lineups " +
                        "where ts.league.id = :leagueId and ts.season =: season and ts.team.id in :teamIds", TeamStatistics.class)
                .setParameter("leagueId", leagueId)
                .setParameter("season", season)
                .setParameter("teamIds", teamIds)
                .getResultList();
    }

    public List<TeamStatistics> findAllById(Long id){
        return em.createQuery("select t from Team t where t.id = :id", TeamStatistics.class)
                .setParameter("id", id)
                .getResultList();
    }

    public TeamStatistics findByTeamNameAndAndSeason(String teamName, int season) {
        return em.createQuery("select ts from TeamStatistics ts " +
                        "where ts.team.name = :teamName and ts.season = :season", TeamStatistics.class)
                .setParameter("teamName", teamName)
                .setParameter("season", season)
                .getResultList().get(0);
    }

}
