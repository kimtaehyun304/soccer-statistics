package com.daelim.sfa.repository.player;

import com.daelim.sfa.domain.player.Player;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Locale;

@Repository
@RequiredArgsConstructor
public class PlayerRepository {

    private final EntityManager em;

    public void flush() {
        em.flush();
    }

    public void save(Player player) {
        em.persist(player);
    }

    public void updateTeamInPlayerId(Long teamId, List<Long> list) {

        // update player set team_id=? where 1=0 방지
        if(list.isEmpty())
            return;

        em.createQuery("update Player p set p.team.id = :teamId where p.id in :list")
                .setParameter("teamId", teamId)
                .setParameter("list", list)
                .executeUpdate();
    }


    public Player findById(Long id) {
        return em.find(Player.class, id);
    }

    public Player findWithTeamById(Long id) {
        List<Player> players = em.createQuery("select p from Player p join fetch p.team " +
                        "where p.id =: id", Player.class)
                .setParameter("id", id)
                .getResultList();

        return players.get(0);
    }

    public Player findByName(String keyword){
        keyword = keyword.toLowerCase(Locale.ROOT);
        List<Player> players = em.createQuery("select p from Player p where LOWER(CONCAT(p.firstName, ' ', p.lastName)) like CONCAT('%', :keyword, '%')", Player.class)
                .setParameter("keyword", keyword)
                .getResultList();

        return players.isEmpty() ? null : players.get(0);
    }


    public List<Player> findAll(){
        return em.createQuery("select p from Player p", Player.class).getResultList();
    }

    public List<Player> findAllByTeamId(Long teamId) {
        return em.createQuery("select p from Player p where p.team.id =: teamId", Player.class)
                .setParameter("teamId", teamId)
                .getResultList();
    }


    public List<Player> findAllByName(String keyword, int maxResults){
        keyword = keyword.toLowerCase(Locale.ROOT);
        return em.createQuery("select p from Player p where LOWER(CONCAT(p.firstName, ' ', p.lastName)) like CONCAT('%', :keyword, '%')", Player.class)
                .setParameter("keyword", keyword)
                .setMaxResults(maxResults)
                .getResultList();
    }

    public List<Player> findAllInId(List<Long> list){
        return em.createQuery("select p from Player p where p.id in :list", Player.class)
                .setParameter("list", list)
                .getResultList();
    }

    public Player findWithStatisticsByPlayerId(Long playerId) {
        List<Player> players = em.createQuery("select p from Player p join fetch p.statisticsList where p.id = :playerId", Player.class)
                .setParameter("playerId", playerId)
                .getResultList();

        return players.isEmpty() ? null : players.get(0);
    }







}
