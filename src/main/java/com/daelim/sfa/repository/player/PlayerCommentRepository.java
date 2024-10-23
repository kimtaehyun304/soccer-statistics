package com.daelim.sfa.repository.player;


import com.daelim.sfa.domain.player.PlayerComment;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PlayerCommentRepository {

    private final EntityManager em;

    public void save(PlayerComment playerComment) {
        em.persist(playerComment);
    }

    public PlayerComment findOne(Long id) {
        return em.find(PlayerComment.class, id);
    }

    public List<PlayerComment> findAllWithMemberByPlayerId(Long playerId, int page, int maxResults) {
        return em.createQuery("select c from PlayerComment c join fetch c.member where c.player.id = :playerId and c.parent.id is null " +
                        "order by c.createdAt asc", PlayerComment.class)
                .setParameter("playerId", playerId)
                .setFirstResult((page-1)*maxResults)
                .setMaxResults(maxResults)
                .getResultList();
    }

    public Long countByPlayerIdAndParentIdIsNull(Long playerId) {
        List<Long> count = em.createQuery("select count(c) from PlayerComment c where c.player.id = :playerId and c.parent.id is null ", Long.class)
                .setParameter("playerId", playerId)
                .getResultList();
        return count.isEmpty() ? 0 : count.get(0);
    }

}
