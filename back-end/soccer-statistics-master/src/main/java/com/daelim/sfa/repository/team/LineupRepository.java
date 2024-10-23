package com.daelim.sfa.repository.team;

import com.daelim.sfa.domain.team.Lineup;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class LineupRepository {

    private final EntityManager em;

    public void save(Lineup lineup) {
        em.persist(lineup);
    }

    public Lineup findById(Long id) {
        return em.find(Lineup.class, id);
    }

    public List<Lineup> findAll(){
        return em.createQuery("select l from Lineup l", Lineup.class).getResultList();
    }

    public List<Lineup> findAllByTeamStatisticsId(Long teamStatisticsId) {
        return em.createQuery("select l from Lineup l where l.teamStatistics.id = :teamStatisticsId", Lineup.class)
                .setParameter("teamStatisticsId", teamStatisticsId)
                .getResultList();
    }

}
