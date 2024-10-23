package com.daelim.sfa.repository.query;

import com.daelim.sfa.dto.ranking.PlayerRankingDto;
import com.daelim.sfa.dto.ranking.TeamRankingDto;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class TeamStatisticsQueryRepository {

    private final EntityManager em;

    public List<TeamRankingDto> findAllByLeagueNameAndLeagueSeason(String leagueName, int leagueSeason) {
        List<TeamRankingDto> teamRankings = new ArrayList<>();

        if (leagueName == null) {
            //teamRankings = em.createQuery("select new com.daelim.sfa.dto.ranking.TeamRankingDto(t.logo, t.name, ts.goals.forTotal, ts.goals.againstTotal, ts.fixtures.wins, ts.fixtures.losses, CONCAT(TRUNCATE(ts.fixtures.wins*100/ts.fixtures.played, 0), '%') ) " +
            teamRankings = em.createQuery("select new com.daelim.sfa.dto.ranking.TeamRankingDto(t.logo, t.name, ts.goals.forTotal, ts.goals.againstTotal, ts.fixtures.played, ts.fixtures.wins, ts.fixtures.losses, ts.fixtures.draws, (ts.fixtures.wins*3)+ts.fixtures.draws)  " +
                            "from TeamStatistics ts join ts.team t " +
                            "where ts.season =:leagueSeason " +
                            "order by (ts.fixtures.wins*3)+ts.fixtures.draws desc", TeamRankingDto.class)
                    .setParameter("leagueSeason", leagueSeason)
                    .getResultList();
        } else {
            teamRankings = em.createQuery("select new com.daelim.sfa.dto.ranking.TeamRankingDto(t.logo, t.name, ts.goals.forTotal, ts.goals.againstTotal, ts.fixtures.played, ts.fixtures.wins, ts.fixtures.losses, ts.fixtures.draws, (ts.fixtures.wins*3)+ts.fixtures.draws)  " +
                            "from TeamStatistics ts join ts.team t " +
                            "where ts.league.name = :leagueName and ts.season =:leagueSeason " +
                            "order by (ts.fixtures.wins*3)+ts.fixtures.draws desc", TeamRankingDto.class)
                    .setParameter("leagueName", leagueName)
                    .setParameter("leagueSeason", leagueSeason)
                    .getResultList();
        }

        int i = 1;
        for (TeamRankingDto teamRanking : teamRankings) {
            teamRanking.addRanking(i);
            i++;
        }

        return teamRankings;
    }
}
