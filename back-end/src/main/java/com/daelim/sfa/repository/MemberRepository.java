package com.daelim.sfa.repository;

import com.daelim.sfa.domain.Member;
import com.daelim.sfa.domain.team.Team;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Locale;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    private final EntityManager em;

    public void save(Member member) {
        em.persist(member);
    }

    public Member findById(Long id) {
        return em.find(Member.class, id);
    }

    public Member findByLoginId(String loginId) {
        List<Member> members = em.createQuery("select m from Member m where m.loginId =: loginId", Member.class)
                .setParameter("loginId", loginId)
                .getResultList();
        return members.isEmpty() ? null : members.get(0);
    }

    public List<Member> findAll(){
        return em.createQuery("select m from Member m", Member.class).getResultList();
    }

}
