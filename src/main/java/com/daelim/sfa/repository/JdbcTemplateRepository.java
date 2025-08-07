package com.daelim.sfa.repository;

import com.daelim.sfa.domain.game.GameFixture;
import com.daelim.sfa.domain.player.Player;
import com.daelim.sfa.domain.player.PlayerStatistics;
import com.daelim.sfa.domain.player.PlayerTransfer;
import com.daelim.sfa.domain.team.Lineup;
import com.daelim.sfa.domain.team.Team;
import com.daelim.sfa.domain.team.Venue;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JdbcTemplateRepository {

    private final JdbcTemplate jdbcTemplate;

    private final EntityManager em;

    public void saveTeams(List<Team> teams) {

        jdbcTemplate.batchUpdate("INSERT INTO team(team_id, name, code, country, founded, national, logo, venue_id) values (?, ?, ?, ?, ?, ?, ?, ?)", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, teams.get(i).getId());
                ps.setString(2, teams.get(i).getName());
                ps.setString(3, teams.get(i).getCode());
                ps.setString(4, teams.get(i).getCountry());
                ps.setInt(5, teams.get(i).getFounded());
                ps.setBoolean(6, teams.get(i).isNational());
                ps.setString(7, teams.get(i).getLogo());
                ps.setLong(8, teams.get(i).getVenue().getId());
            }
            @Override
            public int getBatchSize() {
                return teams.size();
            }
        });
    }

    public void saveVenues(List<Venue> venues) {

        jdbcTemplate.batchUpdate("INSERT INTO venue(venue_id, name, address, city, capacity, surface, image) values (?, ?, ?, ?, ?, ?, ?)", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, venues.get(i).getId());
                ps.setString(2, venues.get(i).getName());
                ps.setString(3, venues.get(i).getAddress());
                ps.setString(4, venues.get(i).getCity());
                ps.setInt(5, venues.get(i).getCapacity());
                ps.setString(6, venues.get(i).getSurface());
                ps.setString(7, venues.get(i).getImage());
            }
            @Override
            public int getBatchSize() {
                return venues.size();
            }
        });
    }


    public void savePlayers(List<Player> players) {

        jdbcTemplate.batchUpdate("INSERT INTO player(player_id, first_name, last_name, age, birth_date, birth_country, nationality, height, weight, photo, updated_at) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, players.get(i).getId());
                ps.setString(2, players.get(i).getFirstName());
                ps.setString(3, players.get(i).getLastName());
                ps.setInt(4, players.get(i).getAge());

                LocalDate localDateBirthDate = players.get(i).getBirth().getDate();
                Date dateBirthDate = null;
                if (localDateBirthDate != null) dateBirthDate = Date.valueOf(localDateBirthDate);

                ps.setDate(5, dateBirthDate);
                ps.setString(6, players.get(i).getBirth().getCountry());
                ps.setString(7, players.get(i).getNationality());
                ps.setString(8, players.get(i).getHeight());
                ps.setString(9, players.get(i).getWeight());
                ps.setString(10, players.get(i).getPhoto());
                ps.setTimestamp(11, Timestamp.valueOf(LocalDateTime.now()));

                //ps.setBoolean(10, players.get(i).isInjured());
                // playerSquad 조회 로직에서 업데이트로 대신
                //ps.setLong(12, players.get(i).getTeam().getId());
                //ps.setInt(13, players.get(i).getNumber());
                //ps.setString(14, players.get(i).getPosition().toString());
            }
            @Override
            public int getBatchSize() {
                return players.size();
            }
        });
    }


    public void savePlayerStatistics(List<PlayerStatistics> PlayerStatisticsList) {

        jdbcTemplate.batchUpdate("INSERT INTO player_statistics(player_id, team_id, league_id, position, rating, shots_total, shots_on, goals_total, goals_conceded, goals_assists, goals_saves, passes_total, passes_key, passes_accuracy, tackles, dribbles_attempts, dribbles_success, fouls_drawn, fouls_committed, yellow_total, red_total, season) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ,?, ?, ?)", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, PlayerStatisticsList.get(i).getPlayer().getId());
                ps.setLong(2, PlayerStatisticsList.get(i).getTeam().getId());
                ps.setLong(3, PlayerStatisticsList.get(i).getLeague().getId());
                ps.setString(4, PlayerStatisticsList.get(i).getPosition());
                ps.setDouble(5, PlayerStatisticsList.get(i).getRating());
                ps.setInt(6, PlayerStatisticsList.get(i).getShots().getTotal());
                ps.setInt(7, PlayerStatisticsList.get(i).getShots().getOn());
                ps.setInt(8, PlayerStatisticsList.get(i).getGoals().getTotal());
                ps.setInt(9, PlayerStatisticsList.get(i).getGoals().getConceded());
                ps.setInt(10, PlayerStatisticsList.get(i).getGoals().getAssists());
                ps.setInt(11, PlayerStatisticsList.get(i).getGoals().getSaves());
                ps.setInt(12, PlayerStatisticsList.get(i).getPasses().getTotal());
                ps.setInt(13, PlayerStatisticsList.get(i).getPasses().getKey());
                ps.setInt(14, PlayerStatisticsList.get(i).getPasses().getAccuracy());
                ps.setInt(15, PlayerStatisticsList.get(i).getTackles());
                ps.setInt(16, PlayerStatisticsList.get(i).getDribbles().getAttempts());
                ps.setInt(17, PlayerStatisticsList.get(i).getDribbles().getSuccess());
                ps.setInt(18, PlayerStatisticsList.get(i).getFouls().getDrawn());
                ps.setInt(19, PlayerStatisticsList.get(i).getFouls().getCommitted());
                ps.setInt(20, PlayerStatisticsList.get(i).getCards().getYellowTotal());
                ps.setInt(21, PlayerStatisticsList.get(i).getCards().getRedTotal());
                ps.setInt(22, PlayerStatisticsList.get(i).getSeason());
            }
            @Override
            public int getBatchSize() {
                return PlayerStatisticsList.size();
            }
        });
    }

    /*
    public void saveTeamStatistics(List<TeamStatistics> teamStatisticsList) {

        jdbcTemplate.batchUpdate("INSERT INTO team_statistics(team_id, league_id, played, wins, losses, draws, forTotal, againstTotal, yellowTotal, redTotal) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, teamStatisticsList.get(i).getTeam().getId());
                ps.setLong(2, teamStatisticsList.get(i).getLeague().getId());
                ps.setInt(3, teamStatisticsList.get(i).getFixtures().getPlayed());
                ps.setInt(4, teamStatisticsList.get(i).getFixtures().getWins());
                ps.setInt(5, teamStatisticsList.get(i).getFixtures().getLosses());
                ps.setInt(6, teamStatisticsList.get(i).getFixtures().getDraws());
                ps.setInt(7, teamStatisticsList.get(i).getGoals().getForTotal());
                ps.setInt(8, teamStatisticsList.get(i).getGoals().getAgainstTotal());
                ps.setInt(9, teamStatisticsList.get(i).getCards().getYellowTotal());
                ps.setInt(10, teamStatisticsList.get(i).getCards().getRedTotal());
            }
            @Override
            public int getBatchSize() {
                return teamStatisticsList.size();
            }
        });
    }
     */

    public void saveLineups(List<Lineup> lineups) {

        jdbcTemplate.batchUpdate("INSERT INTO lineup(team_statistics_id, formation, played) values (?, ?, ?)", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, lineups.get(i).getTeamStatistics().getId());
                ps.setString(2, lineups.get(i).getFormation());
                ps.setInt(3, lineups.get(i).getPlayed());
            }
            @Override
            public int getBatchSize() {
                return lineups.size();
            }
        });
    }

    public void saveGameFixtures(List<GameFixture> gameFixtures) {

        jdbcTemplate.batchUpdate("INSERT INTO game_fixture(game_fixture_id, referee, timezone, date, venue_id, home_team_id, league_id, season, team1_id, team2_id, team1_goals, team2_goals, winner_team_id) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, gameFixtures.get(i).getId());
                ps.setString(2, gameFixtures.get(i).getReferee());
                ps.setString(3, gameFixtures.get(i).getTimezone());
                ps.setTimestamp(4, Timestamp.valueOf(gameFixtures.get(i).getDate()));

                if(gameFixtures.get(i).getVenue().getId() == null)
                    ps.setNull(5, java.sql.Types.BIGINT);
                else
                    ps.setLong(5, gameFixtures.get(i).getVenue().getId());

                ps.setLong(6, gameFixtures.get(i).getHomeTeam().getId());
                ps.setLong(7, gameFixtures.get(i).getLeague().getId());
                ps.setInt(8, gameFixtures.get(i).getSeason());
                ps.setLong(9, gameFixtures.get(i).getTeam1().getId());
                ps.setLong(10, gameFixtures.get(i).getTeam2().getId());

                if(gameFixtures.get(i).getTeam1Goals() == null)
                    ps.setNull(11, java.sql.Types.BIGINT);
                else
                    ps.setInt(11, gameFixtures.get(i).getTeam1Goals());

                if(gameFixtures.get(i).getTeam1Goals() == null)
                    ps.setNull(12, java.sql.Types.BIGINT);
                else
                    ps.setInt(12, gameFixtures.get(i).getTeam2Goals());

                if(gameFixtures.get(i).getWinnerTeam().getId() == null)
                    ps.setNull(13, java.sql.Types.BIGINT);
                else
                    ps.setLong(13, gameFixtures.get(i).getWinnerTeam().getId());
            }
            @Override
            public int getBatchSize() {
                return gameFixtures.size();
            }
        });
    }

    public void savePlayerTransfers(List<PlayerTransfer> playerTransfers) {

        jdbcTemplate.batchUpdate("INSERT INTO player_transfer(player_id, date, type, in_team_id, out_team_id, updated_at) values (?, ?, ?, ?, ?, ?)", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, playerTransfers.get(i).getPlayer().getId());
                LocalDate localDateDate = playerTransfers.get(i).getDate();

                Date dateDate = null;
                if (localDateDate != null)
                    dateDate = Date.valueOf(localDateDate);
                ps.setDate(2, dateDate);

                ps.setString(3, playerTransfers.get(i).getType());

                if(playerTransfers.get(i).getInTeam() == null)
                    ps.setNull(4, java.sql.Types.BIGINT);
                else
                    ps.setLong(4, playerTransfers.get(i).getInTeam().getId());

                if(playerTransfers.get(i).getOutTeam() == null)
                    ps.setNull(5, java.sql.Types.BIGINT);
                else
                    ps.setLong(5, playerTransfers.get(i).getOutTeam().getId());

                ps.setTimestamp(6, Timestamp.valueOf(playerTransfers.get(i).getUpdatedAt()));
            }
            @Override
            public int getBatchSize() {
                return playerTransfers.size();
            }
        });
    }

}
