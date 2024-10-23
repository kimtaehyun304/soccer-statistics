package com.daelim.sfa.repository.team;


import com.daelim.sfa.domain.player.PlayerComment;
import com.daelim.sfa.domain.team.TeamComment;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TeamCommentRepository {

    private final EntityManager em;

    public void save(TeamComment teamComment) {
        em.persist(teamComment);
    }

    public TeamComment findOne(Long id) {
        return em.find(TeamComment.class, id);
    }

    public List<TeamComment> findAllWithMemberByTeamId(Long teamId, int page, int maxResults) {
        return em.createQuery("select c from TeamComment c join fetch c.member where c.team.id = :teamId and c.parent.id is null " +
                        "order by c.createdAt asc", TeamComment.class)
                .setParameter("teamId", teamId)
                .setFirstResult((page-1)*maxResults)
                .setMaxResults(maxResults)
                .getResultList();
    }

    public Long countByTeamIdAndParentId(Long teamId) {
        List<Long> count = em.createQuery("select count(c) from TeamComment c where c.team.id = :teamId and c.parent.id is null ", Long.class)
                .setParameter("teamId", teamId)
                .getResultList();
        return count.isEmpty() ? 0 : count.get(0);
    }

}
