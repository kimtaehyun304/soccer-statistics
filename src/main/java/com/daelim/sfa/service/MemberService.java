package com.daelim.sfa.service;

import com.daelim.sfa.domain.Member;
import com.daelim.sfa.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public Long save(Member member) {
        Member findMember = memberRepository.findByLoginId(member.getLoginId());
        if (findMember == null)
            memberRepository.save(member);
        return member.getId();
    }
}
