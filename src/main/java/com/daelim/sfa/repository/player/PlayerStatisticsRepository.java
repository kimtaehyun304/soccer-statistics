package com.daelim.sfa.repository.player;

import com.daelim.sfa.domain.player.PlayerStatistics;
import com.daelim.sfa.dto.player.StatRankingDto;
import com.daelim.sfa.dto.ranking.PlayerRankingDto;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class PlayerStatisticsRepository {

    private final EntityManager em;

    public void save(PlayerStatistics playerStatistics) {
        em.persist(playerStatistics);
    }

    public PlayerStatistics findById(Long id) {
        return em.find(PlayerStatistics.class, id);
    }

    public List<PlayerStatistics> findAll(){
        return em.createQuery("select p from PlayerStatistics p", PlayerStatistics.class).getResultList();
    }

    public List<PlayerStatistics> findAllByPlayerId(Long playerId){
        return em.createQuery("select p from PlayerStatistics p where p.player.id = :playerId", PlayerStatistics.class)
                .setParameter("playerId", playerId)
                .getResultList();
    }

    public List<PlayerStatistics> findAllWithLeagueByPlayerIdAndLeagueNameAndSeason(Long playerId, String leagueName, int season){
        return em.createQuery("select ps from PlayerStatistics ps join fetch ps.league l " +
                        "where ps.player.id = :playerId and l.name =: leagueName and ps.season = :season", PlayerStatistics.class)
                .setParameter("playerId", playerId)
                .setParameter("leagueName", leagueName)
                .setParameter("season", season)
                .getResultList();
    }

    public List<PlayerStatistics> findAllWithLeagueByLeagueIdAndSeason(Long leagueId, Integer season){
        return em.createQuery("select ps from PlayerStatistics ps join fetch ps.league l " +
                        "where ps.league.id =: leagueId and ps.season = :season", PlayerStatistics.class)
                .setParameter("leagueId", leagueId)
                .setParameter("season", season)
                .getResultList();
    }

    public List<PlayerStatistics> findAllWithLeagueByPlayerIdAndSeason(Long playerId, Integer season){

        if(season == null){
            return em.createQuery("select ps from PlayerStatistics ps join fetch ps.league l " +
                            "where ps.player.id = :playerId " +
                            "order by ps.season desc ", PlayerStatistics.class)
                    .setParameter("playerId", playerId)
                    .getResultList();
        }else {
            return em.createQuery("select ps from PlayerStatistics ps join fetch ps.league l " +
                            "where ps.player.id = :playerId and ps.season = :season", PlayerStatistics.class)
                    .setParameter("playerId", playerId)
                    .setParameter("season", season)
                    .getResultList();
        }

    }

    // 스텟 순위 매기는 메서드입니다 (총 5스텟)
    // 스탯이 0이면 랭킹에 올리지 않습니다.
    public StatRankingDto findStatRankingByLeagueIdAndSeasonPlayerIdAndPosition(Long leagueId, Integer season, Long playerId, String position){

        int goalsRanking = 0;
        int passesRanking = 0;
        int shotsRanking = 0;
        int savesRanking = 0;
        int assistsRanking = 0;

        List<PlayerStatistics> playerStatisticsList;
        List<PlayerRankingDto> playerRankingDtos;

        // goalsRanking
        playerStatisticsList= em.createQuery("select ps from PlayerStatistics ps join fetch ps.league l " +
                        "where ps.league.id =: leagueId and ps.season = :season and ps.position = :position " +
                        "and ps.goals.total != 0" +
                        "order by ps.goals.total desc", PlayerStatistics.class)
                .setParameter("leagueId", leagueId)
                .setParameter("season", season)
                .setParameter("position", position)
                .getResultList();

        playerRankingDtos = playerStatisticsList.stream().map(ps -> new PlayerRankingDto(ps.getPlayer().getId())).toList();

        if(!playerRankingDtos.isEmpty()) {
            for (int i = 0; i < playerRankingDtos.size(); i++) {
                if (Objects.equals(playerRankingDtos.get(i).getPlayerId(), playerId)) {
                    goalsRanking = i + 1;
                    break;
                }
            }
        }

        //passesRanking
        playerStatisticsList = em.createQuery("select ps from PlayerStatistics ps join fetch ps.league l " +
                        "where ps.league.id =: leagueId and ps.season = :season and ps.position = :position " +
                        "and ps.passes.total != 0" +
                        "order by ps.passes.total desc", PlayerStatistics.class)
                .setParameter("leagueId", leagueId)
                .setParameter("season", season)
                .setParameter("position", position)
                .getResultList();

        playerRankingDtos = playerStatisticsList.stream().map(ps -> new PlayerRankingDto(ps.getPlayer().getId())).toList();

        if(!playerRankingDtos.isEmpty()) {
            for (int i = 0; i < playerRankingDtos.size(); i++) {
                if (Objects.equals(playerRankingDtos.get(i).getPlayerId(), playerId)) {
                    passesRanking = i + 1;
                    break;
                }
            }
        }

        //shotsRanking
        playerStatisticsList = em.createQuery("select ps from PlayerStatistics ps join fetch ps.league l " +
                        "where ps.league.id =: leagueId and ps.season = :season and ps.position = :position " +
                        "and ps.shots.total != 0" +
                        "order by ps.shots.total desc", PlayerStatistics.class)
                .setParameter("leagueId", leagueId)
                .setParameter("season", season)
                .setParameter("position", position)
                .getResultList();

        playerRankingDtos = playerStatisticsList.stream().map(ps -> new PlayerRankingDto(ps.getPlayer().getId())).toList();

        if(!playerRankingDtos.isEmpty()) {
            for (int i = 0; i < playerRankingDtos.size(); i++) {
                if (Objects.equals(playerRankingDtos.get(i).getPlayerId(), playerId)) {
                    shotsRanking = i + 1;
                    break;
                }
            }
        }

        //savesRanking
        playerStatisticsList = em.createQuery("select ps from PlayerStatistics ps join fetch ps.league l " +
                        "where ps.league.id =: leagueId and ps.season = :season and ps.position = :position " +
                        "and ps.goals.saves != 0" +
                        "order by ps.goals.saves desc", PlayerStatistics.class)
                .setParameter("leagueId", leagueId)
                .setParameter("season", season)
                .setParameter("position", position)
                .getResultList();

        playerRankingDtos = playerStatisticsList.stream().map(ps -> new PlayerRankingDto(ps.getPlayer().getId())).toList();

        if(!playerRankingDtos.isEmpty()) {
            for (int i = 0; i < playerRankingDtos.size(); i++) {
                if (Objects.equals(playerRankingDtos.get(i).getPlayerId(), playerId)) {
                    savesRanking = i + 1;
                    break;
                }
            }
        }
        //assistsRanking
        playerStatisticsList = em.createQuery("select ps from PlayerStatistics ps join fetch ps.league l " +
                        "where ps.league.id =: leagueId and ps.season = :season and ps.position = :position " +
                        "and ps.goals.assists != 0" +
                        "order by ps.goals.assists desc", PlayerStatistics.class)
                .setParameter("leagueId", leagueId)
                .setParameter("season", season)
                .setParameter("position", position)
                .getResultList();

        playerRankingDtos = playerStatisticsList.stream().map(ps -> new PlayerRankingDto(ps.getPlayer().getId())).toList();

        if(!playerRankingDtos.isEmpty()) {
            for (int i = 0; i < playerRankingDtos.size(); i++) {
                if (Objects.equals(playerRankingDtos.get(i).getPlayerId(), playerId)) {
                    assistsRanking = i + 1;
                    break;
                }
            }
        }

        StatRankingDto statRankingDto = new StatRankingDto(goalsRanking, passesRanking, shotsRanking, savesRanking, assistsRanking);

        return statRankingDto;

    }

}
