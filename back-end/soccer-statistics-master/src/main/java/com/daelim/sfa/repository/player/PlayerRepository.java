package com.daelim.sfa.repository.player;

import com.daelim.sfa.domain.player.Player;
import com.daelim.sfa.domain.team.Team;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Locale;

@Repository
@RequiredArgsConstructor
public class PlayerRepository {

    private final EntityManager em;

    public void save(Player player) {
        em.persist(player);
    }

    public Player findById(Long id) {
        return em.find(Player.class, id);
    }

    public List<Player> findAll(){
        return em.createQuery("select p from Player p", Player.class).getResultList();
    }

    public Player findByName(String keyword){
        keyword = keyword.toLowerCase(Locale.ROOT);
        List<Player> players = em.createQuery("select p from Player p where LOWER(CONCAT(p.firstName, ' ', p.lastName)) like CONCAT('%', :keyword, '%')", Player.class)
                .setParameter("keyword", keyword)
                .getResultList();

        return players.isEmpty() ? null : players.get(0);
    }

    public List<Player> findAllByName(String keyword, int maxResults){
        keyword = keyword.toLowerCase(Locale.ROOT);
        return em.createQuery("select p from Player p where LOWER(CONCAT(p.firstName, ' ', p.lastName)) like CONCAT('%', :keyword, '%')", Player.class)
                .setParameter("keyword", keyword)
                .setMaxResults(maxResults)
                .getResultList();
    }

    public List<Player> findAllById(Long id){
        return em.createQuery("select p from Player p where p.id = :id", Player.class)
                .setParameter("id", id)
                .getResultList();
    }




}
