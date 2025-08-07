package com.daelim.sfa.service;

import com.daelim.sfa.domain.team.Cards;
import com.daelim.sfa.domain.team.Fixtures;
import com.daelim.sfa.domain.team.TeamStatistics;
import com.daelim.sfa.domain.team.TeamStatisticsGoals;
import com.daelim.sfa.repository.team.TeamStatisticsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TeamStatisticsService {

    private final TeamStatisticsRepository teamStatisticsRepository;

    public Long updateTeamStatistics(Long teamStatisticsId, Fixtures fixtures, TeamStatisticsGoals goals, Cards cards) {
        TeamStatistics foundTeamStatistics = teamStatisticsRepository.findById(teamStatisticsId);
        foundTeamStatistics.updateTeamStatistics(fixtures, goals, cards);
        return foundTeamStatistics.getId();
    }

}
