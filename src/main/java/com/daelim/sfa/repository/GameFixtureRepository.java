package com.daelim.sfa.repository;

import com.daelim.sfa.domain.game.GameFixture;
import com.daelim.sfa.domain.team.Team;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class GameFixtureRepository {

    private final EntityManager em;

    public void save(GameFixture gameFixture) {
        em.persist(gameFixture);
    }

    public GameFixture findById(Long id) {
        return em.find(GameFixture.class, id);
    }

    public List<GameFixture> findAll() {
        return em.createQuery("select g from GameFixture g", GameFixture.class).getResultList();
    }

    /*
    public List<GameFixture> findAllByTeamId(Long teamId) {
        return em.createQuery("select g from GameFixture g join fetch g.team1 join fetch g.team2 where g.team1.id = :teamId or g.team2.id = :teamId", GameFixture.class)
                .setParameter("teamId", teamId)
                .getResultList();
    }
     */

    public List<GameFixture> findAllByTeamIdAndLeagueIdAndSeason(Long teamId, Long leagueId, int season) {
        LocalDate today = ZonedDateTime.now(ZoneId.of("Europe/London")).toLocalDate();
        return em.createQuery("select g from GameFixture g join fetch g.team1 join fetch g.team2 " +
                        "where (g.team1.id = :teamId or g.team2.id = :teamId) and g.league.id = :leagueId and g.season = :season and g.date <= :today and g.referee != null", GameFixture.class)
                .setParameter("teamId", teamId)
                .setParameter("leagueId", leagueId)
                .setParameter("season", season)
                .setParameter("today", today)
                .getResultList();
    }

    // 시차가 있지만 매일 08시에 갱신하니까 시차 상관없음 ex) 영국 10-21 08:00 / 한국 10-21 16:00
    public List<GameFixture> findAllByDateAndLeagueIdAndSeason(LocalDate date, Long leagueId, int season) {
        return em.createQuery("SELECT g FROM GameFixture g " +
                        "WHERE g.date = :date and g.league.id = :leagueId and season =: season", GameFixture.class)
                .setParameter("date", date)
                .setParameter("leagueId", leagueId)
                .setParameter("season", season)
                .getResultList();

    }

    // 영국 시간을 기준으로 조회
    public List<GameFixture> findAllByThisWeek() {
        LocalDate today = ZonedDateTime.now(ZoneId.of("Europe/London")).toLocalDate();
        LocalDate sunday = today.with(DayOfWeek.SUNDAY);

        // JPQL 쿼리로 이번 주에 해당하는 경기 일정을 조회
        return em.createQuery("SELECT g FROM GameFixture g WHERE g.date BETWEEN :today AND :sunday", GameFixture.class)
                .setParameter("today", today)
                .setParameter("sunday", sunday)
                .getResultList();
    }


}
