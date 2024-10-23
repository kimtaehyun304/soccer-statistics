package com.daelim.sfa.service;

import com.daelim.sfa.domain.player.*;
import com.daelim.sfa.domain.team.Cards;
import com.daelim.sfa.repository.player.PlayerStatisticsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PlayerStatisticsService {

    private final PlayerStatisticsRepository playerStatisticsRepository;

    public Long updatePlayerStatistics(Long playerStatisticsId, Double rating, Shots shots, PlayerStatisticsGoals goals, Passes passes, int tackles, Dribbles dribbles, Fouls fouls, Cards cards) {
        // log.info("updatePlayerStatistics 메서드 실행");
        // 팀 소속 멤버 수 만큼 select 쿼리 발생함
        PlayerStatistics foundPlayerStatistics = playerStatisticsRepository.findById(playerStatisticsId);
        foundPlayerStatistics.updatePlayerStatistics(rating, shots, goals, passes, tackles, dribbles, fouls, cards);
        return foundPlayerStatistics.getId();
    }

}
