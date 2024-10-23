package com.daelim.sfa.service;

import com.daelim.sfa.domain.game.GameFixture;
import com.daelim.sfa.domain.team.Team;
import com.daelim.sfa.repository.GameFixtureRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class GameFixtureService {

    private final GameFixtureRepository gameFixtureRepository;

    public Long updateFinishedGame(Long gameFixtureId, String referee, Team winnerTeam, int team1Goals, int team2Goals) {
        //log.info("updateFinishedGame 메서드 실행");
        GameFixture foundGameFixture = gameFixtureRepository.findById(gameFixtureId);
        foundGameFixture.updateFinishedGame(referee, winnerTeam, team1Goals, team2Goals);
        return foundGameFixture.getId();
    }

}
