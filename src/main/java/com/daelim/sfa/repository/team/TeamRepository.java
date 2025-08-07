package com.daelim.sfa.repository.team;

import com.daelim.sfa.domain.player.Player;
import com.daelim.sfa.domain.team.Team;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

@Repository
@RequiredArgsConstructor
public class TeamRepository {

    private final EntityManager em;

    public void save(Team team) {
        em.persist(team);
    }

    public Team findById(Long id) {
        return em.find(Team.class, id);
    }

    public List<Team> findAll(){
        return em.createQuery("select t from Team t", Team.class).getResultList();
    }

    public Team findByName(String keyword){
        keyword = keyword.toLowerCase(Locale.ROOT);
        List<Team> teams = em.createQuery("select t from Team t where LOWER(t.name) like concat('%', :keyword, '%')", Team.class)
                .setParameter("keyword", keyword)
                .getResultList();
        return teams.isEmpty() ? null : teams.get(0);
    }

    public List<Team> findAllByName(String keyword, int maxResults){
        keyword = keyword.toLowerCase(Locale.ROOT);
        return em.createQuery("select t from Team t where LOWER(t.name) like concat('%', :keyword, '%')", Team.class)
                .setParameter("keyword", keyword)
                .setMaxResults(maxResults)
                .getResultList();
    }

    public List<Team> findAllInId(Collection<Long> list){
        return em.createQuery("select t from Team t where t.id in :list", Team.class)
                .setParameter("list", list)
                .getResultList();
    }


    public List<Team> findAllByCountry(String country){
        return em.createQuery("select t from Team t where t.country = :country", Team.class)
                .setParameter("country", country)
                .getResultList();
    }

    public Team findWithPlayersByTeamId(Long teamId){
        List<Team> teams = em.createQuery("select t from Team t join fetch t.players " +
                        "where t.id = :teamId", Team.class)
                .setParameter("teamId", teamId)
                .getResultList();

        return teams.isEmpty() ? null : teams.get(0);
    }

}
