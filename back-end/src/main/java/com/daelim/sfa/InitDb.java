package com.daelim.sfa;

import com.daelim.sfa.domain.*;
import com.daelim.sfa.domain.game.GameFixture;
import com.daelim.sfa.domain.player.*;
import com.daelim.sfa.domain.team.*;
import com.daelim.sfa.repository.*;
import com.daelim.sfa.repository.player.PlayerRepository;
import com.daelim.sfa.repository.player.PlayerStatisticsRepository;
import com.daelim.sfa.repository.team.TeamRepository;
import com.daelim.sfa.repository.team.TeamStatisticsRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class InitDb {

    private final InitService initService;

    @PostConstruct
    public void init() throws InterruptedException {

        // 4대 리그 모두 완료
        //initService.initTeamsInformation();
        //initService.initTeamStatistics();
        //initService.initPlayerAndPlayerStatistics();
        //initService.initGameFixtures();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {

        @Value("${rapidApiKey}")
        private String rapidApiKey;
        private final InitRepository initRepository;
        private final ObjectMapper objectMapper;
        private final TeamRepository teamRepository;
        private final TeamStatisticsRepository teamStatisticsRepository;
        private final PlayerStatisticsRepository playerStatisticsRepository;
        private final PlayerRepository playerRepository;

        public void initTeamsInformation() {
            log.info("initTeamsInformation 메서드 실행");
            Long leagueId = 78L;
            int season = 2023;

            Map<String, Object> map = RestClient.create().get()
                    .uri("https://api-football-v1.p.rapidapi.com/v3/teams?league={leagueId}&season={season}", leagueId, season)
                    .header("x-rapidapi-host", "api-football-v1.p.rapidapi.com")
                    .header("x-rapidapi-key", rapidApiKey)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });

            List<Map<String, Object>> maps = (List<Map<String, Object>>) map.get("response");

            List<Team> teams = new ArrayList<>();
            List<Venue> venues = new ArrayList<>();
            Set<Long> venueIds = new HashSet<>();

            for (Map<String, Object> m : maps) {
                Venue venue = objectMapper.convertValue(m.get("venue"), Venue.class);
                if(!venueIds.contains(venue.getId())){
                    venueIds.add(venue.getId());
                    venues.add(venue);
                }

                Team team = objectMapper.convertValue(m.get("team"), Team.class);
                team.addVenue(venue);
                teams.add(team);
            }

            initRepository.saveVenues(venues);
            initRepository.saveTeams(teams);
            log.info("initTeamsInformation 메서드 완료");
        }

        // rapidApi가 팀 파라미터를 필수적으로 요구한다.
        public void initTeamStatistics() {
            log.info("initTeamStatistics 메서드 실행");

            List<Team> teams = teamRepository.findAllByCountry("Spain");
            List<Long> teamIds = teams.stream().map(t -> t.getId()).toList();

            Long leagueId = 140L;
            int season = 2023;

            //List<TeamStatistics> teamStatisticsList = new ArrayList<>();

            for (Long teamId : teamIds) {
                Map<String, Object> map = RestClient.create().get()
                        .uri("https://api-football-v1.p.rapidapi.com/v3/teams/statistics?league={leagueId}&team={teamId}&season={season}", leagueId, teamId, season)
                        .header("x-rapidapi-host", "api-football-v1.p.rapidapi.com")
                        .header("x-rapidapi-key", rapidApiKey)
                        .retrieve()
                        .body(new ParameterizedTypeReference<>() {});

                Map<String, Object> responseMap = (Map<String, Object>) map.get("response");

                //fixtures
                Map<String, Object> fixturesMap = (Map<String, Object>) responseMap.get("fixtures");

                Map<String, Object> playedMap = (Map<String, Object>) fixturesMap.get("played");
                int playedTotal = (int) playedMap.get("total");

                Map<String, Object> winsMap = (Map<String, Object>) fixturesMap.get("wins");
                int winsTotal = (int) winsMap.get("total");

                Map<String, Object> drawsMap = (Map<String, Object>) fixturesMap.get("draws");
                int drawsTotal = (int) drawsMap.get("total");

                Map<String, Object> losesMap = (Map<String, Object>) fixturesMap.get("loses");
                int losesTotal = (int) losesMap.get("total");

                Fixtures fixtures = Fixtures.builder().played(playedTotal).wins(winsTotal).draws(drawsTotal).losses(losesTotal).build();

                //goals
                Map<String, Object> goalsMap = (Map<String, Object>) responseMap.get("goals");

                Map<String, Object> forMap = (Map<String, Object>) goalsMap.get("for");
                Map<String, Object> forTotalMap = (Map<String, Object>) forMap.get("total");
                int forTotal = (int) forTotalMap.get("total");

                Map<String, Object> againstMap = (Map<String, Object>) goalsMap.get("against");
                Map<String, Object> againstTotalMap = (Map<String, Object>) againstMap.get("total");
                int againstTotal = (int) againstTotalMap.get("total");

                TeamStatisticsGoals goals = TeamStatisticsGoals.builder().forTotal(forTotal).againstTotal(againstTotal).build();

                //cards
                Map<String, Object> cardsMap = (Map<String, Object>) responseMap.get("cards");

                Map<String, Object> yellowMap = (Map<String, Object>) cardsMap.get("yellow");

                List<String> timeLines = Arrays.asList("0-15", "16-30", "31-45", "46-60", "61-75", "76-90", "91-105", "106-120");

                int yellowTotal = 0;
                for (String timeLine : timeLines) {
                    Integer value = (Integer) ((Map<String, Object>) yellowMap.get(timeLine)).get("total");
                    yellowTotal += Optional.ofNullable(value).orElse(0);
                }

                Map<String, Object> redMap = (Map<String, Object>) cardsMap.get("red");

                int redTotal = 0;
                for (String timeLine : timeLines) {
                    Integer value = (Integer) ((Map<String, Object>) redMap.get(timeLine)).get("total");
                    redTotal += Optional.ofNullable(value).orElse(0);
                }

                Cards cards = Cards.builder().yellowTotal(yellowTotal).redTotal(redTotal).build();
                Team team = new Team(teamId);
                League league = new League(leagueId);
                TeamStatistics teamStatistics = TeamStatistics.builder().team(team).league(league).fixtures(fixtures).goals(goals).cards(cards).season(season).build();

                teamStatisticsRepository.save(teamStatistics);

                //lineups
                List<Lineup> lineups = objectMapper.convertValue(responseMap.get("lineups"), new TypeReference<>() {});
                lineups.stream().forEach(l -> l.addTeamStatistics(teamStatistics));
                initRepository.saveLineups(lineups);

            }
            log.info("initTeamStatistics 메서드 완료");
        }

        // PlayerStatistic season은 끝나는 년도가 API 기준입니다. -> DB 저장은 시작하는 년도로 바꿔서 저장했습니다.
        public void initPlayerAndPlayerStatistics() throws InterruptedException {
            log.info("initPlayerAndPlayerStatistics 메서드 실행");

            List<Team> teams = teamRepository.findAllByCountry("Spain");
            List<Long> teamIds = teams.stream().map(t -> t.getId()).toList();

            Long leagueId = 140L;
            int season = 2024;
            League league = new League(leagueId);

            List<Player> players = new ArrayList<>();
            Set<Long> playerIds = new HashSet<>(playerRepository.findAll().stream().map(Player::getId).toList());
            List<PlayerStatistics> playerStatisticsList = new ArrayList<>();

            int apiCount = 0;

            //특정 리그의 모든 팀 조회
            for (Long teamId : teamIds) {
                // 페이징 전체 길이 조회
                Map<String, Object> map = RestClient.create().get()
                        .uri("https://api-football-v1.p.rapidapi.com/v3/players?season={season}&league={leagueId}&team={teamId}", season, leagueId, teamId)
                        .header("x-rapidapi-host", "api-football-v1.p.rapidapi.com")
                        .header("x-rapidapi-key", rapidApiKey)
                        .retrieve()
                        .body(new ParameterizedTypeReference<>() {});

                apiCount++;

                Map<String, Object> pagingMap = (Map<String, Object>) map.get("paging");
                int pagingTotal = (Integer) pagingMap.get("total");

                Team team = new Team(teamId);

                for(int i=1; i<=pagingTotal; i++){

                    Map<String, Object> imap = RestClient.create().get()
                            .uri("https://api-football-v1.p.rapidapi.com/v3/players?season={season}&league={leagueId}&team={teamId}&page={page}", season, leagueId, teamId, i)
                            .header("x-rapidapi-host", "api-football-v1.p.rapidapi.com")
                            .header("x-rapidapi-key", rapidApiKey)
                            .retrieve()
                            .body(new ParameterizedTypeReference<>() {});

                    apiCount++;
                    log.info("current apiCount : {}",apiCount);

                    if(apiCount >= 30) {
                        log.info("API 쿼타 제한 때문에 60초 대기합니다");
                        Thread.sleep(1000 * 61); // 60초 대기
                        apiCount = 0;
                    }

                    List<Map<String, Object>> responseMaps = (List<Map<String, Object>>) imap.get("response");
                    for (Map<String, Object> responseMap : responseMaps) {

                        //System.out.println("responseMap.toString() = " + responseMap.toString());

                        Map<String, Object> playerMap = (Map<String, Object>) responseMap.get("player");

                        // player 초기화
                        Player player = objectMapper.convertValue(playerMap, Player.class);

                        if(playerIds.contains(player.getId())) {
                            log.info("중복된 PK라 players에 안 넣습니다");
                        }else {
                            playerIds.add(player.getId());
                            String firstName = (String) playerMap.get("firstname");
                            String lastName = (String) playerMap.get("lastname");
                            player.addName(firstName, lastName);
                            players.add(player);
                        }

                        // playerStatistics 초기화
                        // 이적 선수는 두개 이상 통계를 가질 가능성이 있습니다
                        List<Map<String, Object>> statisticsMaps = (List<Map<String, Object>>) responseMap.get("statistics");
                        for (Map<String, Object> statisticsMap : statisticsMaps) {
                            Map<String, Object> gamesMap = (Map<String, Object>) statisticsMap.get("games");
                            String position = (String) gamesMap.get("position");

                            Double rating = (double) 0;
                            Object objectRating = gamesMap.get("rating");
                            if(objectRating != null) rating = Double.parseDouble((String) objectRating);

                            Shots shots = objectMapper.convertValue(statisticsMap.get("shots"), Shots.class);
                            PlayerStatisticsGoals goals = objectMapper.convertValue(statisticsMap.get("goals"), PlayerStatisticsGoals.class);

                            Passes passes = objectMapper.convertValue(statisticsMap.get("passes"), Passes.class);

                            Map<String, Object> tacklesMap = (Map<String, Object>) statisticsMap.get("tackles");
                            int tacklesTotal = invokeIntegerOrElse(tacklesMap.get("total"));

                            Dribbles dribbles = objectMapper.convertValue(statisticsMap.get("dribbles"), Dribbles.class);
                            Fouls fouls = objectMapper.convertValue(statisticsMap.get("fouls"), Fouls.class);

                            Map<String, Object> cardsMap = (Map<String, Object>) statisticsMap.get("cards");
                            int yellowTotal = invokeIntegerOrElse(cardsMap.get("yellow"));
                            int redTotal = invokeIntegerOrElse(cardsMap.get("red"));
                            Cards cards = Cards.builder().yellowTotal(yellowTotal).redTotal(redTotal).build();

                            PlayerStatistics playerStatistics = PlayerStatistics.builder().player(player).team(team).league(league).position(position).rating(rating).shots(shots).goals(goals).passes(passes).tackles(tacklesTotal).dribbles(dribbles).fouls(fouls).cards(cards).season(season-1).build();
                            playerStatisticsList.add(playerStatistics);
                        }
                    }
                }
            }

            initRepository.savePlayers(players);
            initRepository.savePlayerStatistics(playerStatisticsList);

            log.info("initPlayerAndPlayerStatistics 메서드 완료");
        }

        // 스쿼드 데이터(팀 멤버 리스트) 안 쓸 예정입니다.
        public void initTeamSquads(){

        }

        public void initGameFixtures(){
            log.info("initGameFixtures 메서드 실행");

            Long leagueId = 140L;
            int season = 2023;

            League league = new League(leagueId);

            Map<String, Object> map = RestClient.create().get()
                        .uri("https://api-football-v1.p.rapidapi.com/v3/fixtures?league={leagueId}&season={season}", leagueId, season)
                        .header("x-rapidapi-host", "api-football-v1.p.rapidapi.com")
                        .header("x-rapidapi-key", rapidApiKey)
                        .retrieve()
                        .body(new ParameterizedTypeReference<>() {});

            //System.out.println("map.toString() = " + map.toString());
            
            
            List<GameFixture> gameFixtures = new ArrayList<>();

            List<Map<String, Object>> responseMaps = (List<Map<String, Object>>) map.get("response");
            for (Map<String, Object> responseMap : responseMaps) {
                Map<String, Object> fixtureMap = (Map<String, Object>) responseMap.get("fixture");

                
                Long fixtureId = Long.valueOf(((Integer) fixtureMap.get("id")));
                String referee = (String) fixtureMap.get("referee");
                String timezone = (String) fixtureMap.get("timezone");
                LocalDate date = OffsetDateTime.parse((String) fixtureMap.get("date")).toLocalDate();

                Integer intVenueId = ((Integer) ((Map<String, Object>) fixtureMap.get("venue")).get("id"));

                Long venueId = null;
                if(intVenueId != null) venueId = Long.valueOf(intVenueId);

                //Long venueId = Long.valueOf(((Integer) ((Map<String, Object>) fixtureMap.get("venue")).get("id")));
                //Object _venueId = (Long) ((Map<String, Object>) fixtureMap.get("venue")).get("id");

                Venue venue = new Venue(venueId);

                Map<String, Object> teamsMap = (Map<String, Object>) responseMap.get("teams");
                //System.out.println("teamsMap.toString() = " + teamsMap.toString());

                Map<String, Object> homeMap = (Map<String, Object>) teamsMap.get("home");
                Map<String, Object> awayMap = (Map<String, Object>) teamsMap.get("away");


                Map<String, Object> goalsMap = (Map<String, Object>) responseMap.get("goals");
                //System.out.println("goalsMap.toString() = " + goalsMap.toString());

                Team homeTeam = new Team(Long.valueOf((Integer) homeMap.get("id")));

                Team team1 = new Team(Long.valueOf((Integer) homeMap.get("id")));
                int team1Goals = (int) goalsMap.get("home");

                Team team2 = new Team(Long.valueOf((Integer) awayMap.get("id")));
                int team2Goals = (int) goalsMap.get("away");

                Boolean team1Win = (Boolean) homeMap.get("winner");
                Long winnerTeamId = null;

                if(team1Win != null)
                    winnerTeamId = team1Win ? team1.getId() : team2.getId();

                Team winnerTeam = new Team(winnerTeamId);

                GameFixture gameFixture = GameFixture.builder().id(fixtureId).referee(referee).timezone(timezone).date(date).venue(venue).homeTeam(homeTeam).league(league).season(season).team1(team1).team2(team2).team1Goals(team1Goals).team2Goals(team2Goals).winnerTeam(winnerTeam).build();
                gameFixtures.add(gameFixture);
            }

            initRepository.saveGameFixtures(gameFixtures);

            log.info("initGameFixtures 메서드 완료");
        }
        
        
        // Integer에 null이 들어오면 기본값 0을 넣어주는 함수
        public int invokeIntegerOrElse(Object objectValue){
            Integer integerValue = (Integer) objectValue;

            if(integerValue == null) return 0;
            else return integerValue;
        }


        // Integer에 null이 들어오면 기본값 0을 넣어주는 함수
        public Double invokeDoubleOrElse(Double doubleValue){
            if(doubleValue == null) return (double) 0;
            else return doubleValue;
        }


    }

}