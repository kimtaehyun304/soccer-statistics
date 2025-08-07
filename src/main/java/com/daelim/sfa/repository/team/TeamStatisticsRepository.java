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

    public List<TeamStatistics> findAll(){
        return em.createQuery("select t from TeamStatistics t", TeamStatistics.class).getResultList();
    }

    public List<TeamStatistics> findAllWithLineUpByLeagueIdAndSeasonInTeamId(Long leagueId, int season, List<Long> teamIds){
        return em.createQuery("select ts from TeamStatistics ts join fetch ts.lineups " +
                        "where ts.league.id = :leagueId and ts.season =: season and ts.team.id in :teamIds", TeamStatistics.class)
                .setParameter("leagueId", leagueId)
                .setParameter("season", season)
                .setParameter("teamIds", teamIds)
                .getResultList();
    }

    public TeamStatistics findWithLineUpByTeamIdAndAndSeason(Long teamId, int season) {
        return em.createQuery("select ts from TeamStatistics ts join fetch ts.lineups " +
                        "where ts.team.id = :teamId and ts.season = :season", TeamStatistics.class)
                .setParameter("teamId", teamId)
                .setParameter("season", season)
                .getResultList().get(0);
    }

    public List<TeamStatistics> findAllByLeagueIdAndSeason(Long leagueId, int season){
        return em.createQuery("select ts from TeamStatistics ts where ts.league.id = :leagueId and ts.season = :season", TeamStatistics.class)
                .setParameter("leagueId", leagueId)
                .setParameter("season", season)
                .getResultList();
    }

    // 현재 db 상황은 팀은 하나의 리그에세먼 활동해서, 리그 Id는 안 필요합니다.
    public List<Integer> findSeasonsByTeamId(Long teamId){
        return em.createQuery("select ts.season from TeamStatistics ts " +
                        "where ts.team.id = :teamId order by ts.season desc", Integer.class)
                .setParameter("teamId", teamId)
                .getResultList();
    }

}
