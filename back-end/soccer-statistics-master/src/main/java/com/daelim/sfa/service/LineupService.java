package com.daelim.sfa.service;

import com.daelim.sfa.domain.team.Lineup;
import com.daelim.sfa.repository.team.LineupRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class LineupService {

    private final LineupRepository lineupRepository;

    public void save(Lineup lineup) {
        //log.info("save 메서드 실행");
        lineupRepository.save(lineup);
    }

    public Long updateLineup(Long lineupId, int played) {
        //log.info("updateLineup 메서드 실행");
        Lineup foundLineUp = lineupRepository.findById(lineupId);
        foundLineUp.updateLineup(played);
        return foundLineUp.getId();
    }

}
