package com.daelim.sfa;

import com.daelim.sfa.domain.League;
import com.daelim.sfa.domain.News;
import com.daelim.sfa.domain.game.GameFixture;
import com.daelim.sfa.domain.player.*;
import com.daelim.sfa.domain.team.*;
import com.daelim.sfa.repository.GameFixtureRepository;
import com.daelim.sfa.repository.JdbcTemplateRepository;
import com.daelim.sfa.repository.NewsRepository;
import com.daelim.sfa.repository.player.PlayerRepository;
import com.daelim.sfa.repository.player.PlayerStatisticsRepository;
import com.daelim.sfa.repository.team.TeamRepository;
import com.daelim.sfa.repository.team.TeamStatisticsRepository;
import com.daelim.sfa.service.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Profile("!local") // local이 아닌 환경에서만 동작
@Component
@Slf4j
@RequiredArgsConstructor
//2024 프리미어 리그만 업데이트 합니다
public class ScheduledTasks {

    private final UpdateService updateService;
    private final NewsRepository newsRepository;
    private final NewsService newsService;
    private final GameFixtureRepository gameFixtureRepository;
    private final TeamStatisticsRepository teamStatisticsRepository;

    @Scheduled(cron = "0 0 13 ? * *", zone = "Asia/Seoul")
    public void saveCrawlingNews() {
        log.info("saveCrawlingNews 메서드 실행");
        String url = "https://www.yna.co.kr/sports/football";
        List<News> newsList = new ArrayList<>();

        try {
            List<News> foundNewsList = newsRepository.findAll();

            if (!foundNewsList.isEmpty())
                newsService.deleteAll();

            // Jsoup을 사용한 크롤링
            Document document = Jsoup.connect(url).get();
            //Element divElement = document.select("div.list-type038").first();

            // nth-child(-n+2)가 안 되서 수동으로 카운트 함
            Elements newsElements = document.select("div.list-type038 ul.list li");

            for (Element newsElement : newsElements) {
                // 송고 시간 추출
                String createdAt = newsElement.select(".txt-time").text();

                // 중간에 광고가 있어서 필터링
                if (createdAt.isEmpty())
                    continue;
                createdAt = LocalDate.now().getYear() + "-" + createdAt;

                // 이미지 URL 추출
                String imageUrl = newsElement.select(".img-con a img").attr("src");

                // 제목 추출
                String title = newsElement.select(".tit-news").text();

                String content = newsElement.select(".lead").text();

                String hrefUrl = newsElement.select(".news-con a").attr("href");

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                News news = News.builder().createdAt(LocalDateTime.parse(createdAt, formatter)).imageUrl(imageUrl).title(title).content(content).hrefUrl(hrefUrl).build();
                newsList.add(news);

                if (newsList.size() == 3)
                    break;
            }

            newsService.saveNewsList(newsList);
            log.info("saveCrawlingNews 메서드 종료");
        } catch (IOException e) {
            log.error("크롤링 실패");
        }
    }

    @Scheduled(cron = "0 00 23 * * ?", zone = "Europe/London")
    public void update() throws Exception {
        log.info("update 스케줄 시작");

        Long leagueId = 39L;
        int season = 2024;

        ZonedDateTime londonTime = ZonedDateTime.now(ZoneId.of("Europe/London"));
        LocalDate todayLondon = londonTime.toLocalDate();

        List<GameFixture> foundGameFixtures = gameFixtureRepository.findAllByDateAndLeagueIdAndSeason(todayLondon, leagueId, season);

        List<TeamStatistics> foundTeamStatisticsList = teamStatisticsRepository.findAllByLeagueIdAndSeason(39L, 2024);
        List<Long> teamIds = foundTeamStatisticsList.stream().map(t -> t.getTeam().getId()).toList();

        try {
            if (foundGameFixtures.isEmpty()) {
                //API 20회 호출
                updateService.updateSquad(teamIds);
            } else {
                //1회 호출
                updateService.updateFinishedGameFixtures(leagueId, season, todayLondon);
            }

            //20회 호출 (경기 있는 날 PM 11시에 호출했는데 RAPID API 갱신이 안되있어서 매일 호출하는 로직으로 바꿧습니다)
            updateService.updateTeamStatisticsAndLineup(teamIds, leagueId, season);

            // 약 60회 호출
            updateService.updatePlayerStatistics(teamIds, leagueId, season);

            updateService.clearApiCount();
            log.info("update 스케줄 완료");
        } catch (Exception e) {
            log.error("update 중 예외 발생", e); // 전체 예외 출력
        }

    }


    @Component
    @RequiredArgsConstructor
    //변경감지를 위해 붙임
    @Transactional
    static class UpdateService {

        @Value("${rapidApiKey}")
        private String rapidApiKey;

        private final GameFixtureRepository gameFixtureRepository;
        private final TeamStatisticsRepository teamStatisticsRepository;
        private final TeamStatisticsService teamStatisticsService;
        private final ObjectMapper objectMapper;
        private final LineupService lineupService;
        private final TeamRepository teamRepository;
        private final PlayerRepository playerRepository;
        private final JdbcTemplateRepository jdbcTemplateRepository;
        private final PlayerStatisticsRepository playerStatisticsRepository;
        private int apiCount;

        private void checkApiCount() throws InterruptedException {
            if (apiCount >= 30) {
                Thread.sleep(1000 * 65);
                apiCount = 0;
            }
            //System.out.println("apiCount: "+apiCount);
        }

        private void clearApiCount() {
            apiCount = 0;
        }

        public void updateSquad(List<Long> teamIds) throws InterruptedException {
            log.info("updateSquads 메서드 실행");

            // RAPID API 호출 줄이려고 올해 승격한 팀만 가져왔습니다.
            List<Team> teams = teamRepository.findAllInId(teamIds);

            // 팀 단위로 나눠서 작업
            for (Team team : teams) {

                checkApiCount();

                Map<String, Object> rootMap = RestClient.create().get()
                        .uri("https://api-football-v1.p.rapidapi.com/v3/players/squads?team={teamId}", team.getId())
                        .header("x-rapidapi-host", "api-football-v1.p.rapidapi.com")
                        .header("x-rapidapi-key", rapidApiKey)
                        .retrieve()
                        .body(new ParameterizedTypeReference<>() {
                        });
                ++apiCount;

                List<Map<String, Object>> responseMaps = (List<Map<String, Object>>) rootMap.get("response");

                Map<Long, Player> rapidSquadMap = new HashMap<>();
                for (Map<String, Object> rm : responseMaps) {
                    List<Map<String, Object>> playerMaps = (List<Map<String, Object>>) rm.get("players");
                    for (Map<String, Object> playerMap : playerMaps) {
                        Player player = objectMapper.convertValue(playerMap, Player.class);
                        rapidSquadMap.put(player.getId(), player);
                    }
                }

                //★새로운 선수 INSERT
                //중복이 들어올리는 없지만, 조회 성능을 위해 List 대신 Set 사용
                Set<Long> rapidPlayerIds = new HashSet<>(rapidSquadMap.keySet());
                List<Player> existingPlayers = playerRepository.findAllInId(rapidPlayerIds.stream().toList());
                Set<Long> existingPlayerIds = existingPlayers.stream()
                        .map(Player::getId)
                        .collect(Collectors.toSet());
                //rapidApi로 호출하면 기존+새로운 선수가 함께 들어있음
                List<Player> newPlayers = searchPlayerProfiles(existingPlayerIds.stream().filter(id -> !rapidPlayerIds.contains(id)).toList());
                jdbcTemplateRepository.savePlayers(newPlayers);

                //★기존 선수 UPDATE
                for (Player player : existingPlayers) {
                    Long playerId = player.getId();
                    Position position = rapidSquadMap.get(playerId).getPosition();
                    player.updateTeamAndPosition(team, position);
                }

                //★이적한 선수 UPDATE
                //영속성 컨텍스트 Map 만들기
                Map<Long, Player> foundSquadMap = new HashMap<>();
                List<Player> foundSquad = playerRepository.findAllByTeamId(team.getId());
                List<Long> foundSquadPlayerIds = foundSquad.stream().map(Player::getId).toList();
                for (Player player : foundSquad) {
                    foundSquadMap.put(player.getId(), player);
                }
                //프리미어 리그 밖으로 이적하면 업데이트를 못 함 -> 오래된 데이터라는 걸 알리기 위해 updatedAt 컬럼 추가
                List<Long> outPlayerIds = foundSquadPlayerIds.stream().filter(id -> !rapidPlayerIds.contains(id)).toList();
                for (Long outPlayerId : outPlayerIds) {
                    Position position = rapidSquadMap.get(outPlayerId).getPosition();
                    foundSquadMap.get(outPlayerId).updateTeamAndPosition(null, position);
                }
            }

            log.info("updateSquads 메서드 완료");
        }

        public void updateFinishedGameFixtures(Long leagueId, int season, LocalDate todayLondon) throws InterruptedException {
            log.info("updateFinishedGameFixtures 스케줄 실행");

            Map<Long, GameFixture> gameFixtureMapByRapidApi = new HashMap<>();
            League league = new League(leagueId);

            checkApiCount();

            Map<String, Object> map = RestClient.create().get()
                    .uri("https://api-football-v1.p.rapidapi.com/v3/fixtures?league={leagueId}&season={season}&date={date}", leagueId, season, todayLondon)
                    .header("x-rapidapi-host", "api-football-v1.p.rapidapi.com")
                    .header("x-rapidapi-key", rapidApiKey)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });
            ++apiCount;

            List<Map<String, Object>> responseMaps = (List<Map<String, Object>>) map.get("response");
            for (Map<String, Object> responseMap : responseMaps) {
                Map<String, Object> fixtureMap = (Map<String, Object>) responseMap.get("fixture");
                Long fixtureId = Long.valueOf(((Integer) fixtureMap.get("id")));
                String referee = (String) fixtureMap.get("referee");
                String timezone = (String) fixtureMap.get("timezone");
                LocalDateTime date = OffsetDateTime.parse((String) fixtureMap.get("date")).toLocalDateTime();

                Integer intVenueId = ((Integer) ((Map<String, Object>) fixtureMap.get("venue")).get("id"));
                Long venueId = null;
                if (intVenueId != null) venueId = Long.valueOf(intVenueId);
                Venue venue = new Venue(venueId);

                Map<String, Object> teamsMap = (Map<String, Object>) responseMap.get("teams");
                Map<String, Object> awayMap = (Map<String, Object>) teamsMap.get("away");
                Map<String, Object> homeMap = (Map<String, Object>) teamsMap.get("home");
                Team homeTeam = new Team(Long.valueOf((Integer) homeMap.get("id")));
                Map<String, Object> goalsMap = (Map<String, Object>) responseMap.get("goals");

                Team team1 = new Team(Long.valueOf((Integer) homeMap.get("id")));
                int team1Goals = (int) goalsMap.get("home");

                Team team2 = new Team(Long.valueOf((Integer) awayMap.get("id")));
                int team2Goals = (int) goalsMap.get("away");

                Boolean team1Win = (Boolean) homeMap.get("winner");
                Long winnerTeamId = null;

                if (team1Win != null) winnerTeamId = team1Win ? team1.getId() : team2.getId();
                Team winnerTeam = (winnerTeamId == null) ? null : new Team(winnerTeamId);

                GameFixture gameFixture = GameFixture.builder().id(fixtureId).referee(referee).timezone(timezone).date(date)
                        .venue(venue).homeTeam(homeTeam).league(league).season(season).team1(team1).team2(team2)
                        .team1Goals(team1Goals).team2Goals(team2Goals).winnerTeam(winnerTeam)
                        .build();
                gameFixtureMapByRapidApi.put(gameFixture.getId(), gameFixture);
            }

            List<GameFixture> foundGameFixtures = gameFixtureRepository.findAllByDateAndLeagueIdAndSeason(todayLondon, leagueId, season);

            //변경감지 로직
            for (GameFixture foundGameFixture : foundGameFixtures) {
                GameFixture gameFixture = gameFixtureMapByRapidApi.get(foundGameFixture.getId());
                String referee = gameFixture.getReferee();
                Team winnerTeam = gameFixture.getWinnerTeam();
                int team1Goals = gameFixture.getTeam1Goals();
                int team2Goals = gameFixture.getTeam2Goals();

                foundGameFixture.updateFinishedGame(referee, winnerTeam, team1Goals, team2Goals);
                //이렇게 하면 변경감지가 안됨! (한 트랜잭션안에서 조회-변경이 일이나야 변경감지 동작)
                //gameFixtureService.updateFinishedGame(foundGameFixture, referee, winnerTeam, team1Goals, team2Goals);
                //gameFixtureService.updateFinishedGame(foundGameFixture.getId(), referee, winnerTeam, team1Goals, team2Goals);
            }
            log.info("updateFinishedGameFixtures 스케쥴 종료");
        }

        public void updateTeamStatisticsAndLineup(List<Long> teamIds, Long leagueId, int season) throws InterruptedException {
            log.info("updateTeamStatisticsAndLineup 메서드 실행");

            List<TeamStatistics> foundTeamStatisticsList = teamStatisticsRepository.findAllByLeagueIdAndSeason(39L, 2024);

            // key: teamId
            Map<Long, TeamStatistics> foundTeamStatisticsMap = new HashMap<>();
            for (TeamStatistics foundTeamStatistics : foundTeamStatisticsList) {
                foundTeamStatisticsMap.put(foundTeamStatistics.getTeam().getId(), foundTeamStatistics);
            }

            //리그&시즌으로 전체 팀 통계 조회 안됨 -> 팀 하나씩 조회
            for (Long teamId : teamIds) {

                checkApiCount();

                Map<String, Object> map = RestClient.create().get()
                        .uri("https://api-football-v1.p.rapidapi.com/v3/teams/statistics?league={leagueId}&team={teamId}&season={season}", leagueId, teamId, season)
                        .header("x-rapidapi-host", "api-football-v1.p.rapidapi.com")
                        .header("x-rapidapi-key", rapidApiKey)
                        .retrieve()
                        .body(new ParameterizedTypeReference<>() {
                        });
                ++apiCount;

                Map<String, Object> responseMap = (Map<String, Object>) map.get("response");
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

                //★팀 통계 UPDATE
                TeamStatistics teamStatisticsByRapidApi = TeamStatistics.builder().team(team).league(league).fixtures(fixtures)
                        .goals(goals).cards(cards).season(season).build();
                TeamStatistics foundTeamStatistics = foundTeamStatisticsMap.get(teamId);
                teamStatisticsService.updateTeamStatistics(foundTeamStatistics.getId(), teamStatisticsByRapidApi.getFixtures(), teamStatisticsByRapidApi.getGoals(), teamStatisticsByRapidApi.getCards());

                //lineups Map 생성
                Map<String, Lineup> foundLineupMap = new HashMap<>();
                List<Lineup> foundLineups = foundTeamStatistics.getLineups();
                for (Lineup foundLineup : foundLineups) {
                    foundLineupMap.put(foundLineup.getFormation(), foundLineup);
                }

                //★새로운 포메이션이면 INSERT, 같은 포메이션의 played 수가 늘면 UPDATE
                List<Lineup> lineupsByRapidApi = objectMapper.convertValue(responseMap.get("lineups"), new TypeReference<>() {});
                for (Lineup lineupByRapidApi : lineupsByRapidApi) {
                    Lineup existingLineup = foundLineupMap.get(lineupByRapidApi.getFormation());
                    if (existingLineup == null) {
                        lineupByRapidApi.addTeamStatistics(foundTeamStatistics);
                        lineupService.save(lineupByRapidApi);
                    } else {
                        existingLineup.updateLineup(lineupByRapidApi.getPlayed());
                    }
                }

            }

            log.info("updateTeamStatisticsAndLineup 메서드 완료");
        }

        public void updatePlayerStatistics(List<Long> teamIds, Long leagueId, Integer season) throws Exception {
            log.info("updatePlayerStatistics 스케줄 실행");

            List<Player> newPlayers = new ArrayList<>();

            //contains() 성능을 위해 List 대신 Set 사용
            Set<Long> existedPlayerIds = new HashSet<>(playerRepository.findAll().stream().map(Player::getId).toList());

            List<PlayerStatistics> newPlayerStatisticsList = new ArrayList<>();
            List<PlayerStatistics> foundPlayerStatisticsList = playerStatisticsRepository.findAllWithLeagueByLeagueIdAndSeason(leagueId, season);

            //map_key: player_id - team_id - league_id - season
            Map<String, PlayerStatistics> foundPlayerStatisticsMap = new HashMap<>();
            for (PlayerStatistics foundPlayerStatistic : foundPlayerStatisticsList) {
                Long playerId = foundPlayerStatistic.getPlayer().getId();
                Long teamId = foundPlayerStatistic.getTeam().getId();
                StringBuilder stringBuilder = new StringBuilder();
                //이적한 선수는 통계를 여러개 가질 수 있어서, 저 4개로 복합 유니크처럼 묶어서 구별함
                String mapKeyByFound = stringBuilder.append(playerId).append("-").append(teamId).append("-").append(leagueId).append("-").append(season).toString();
                foundPlayerStatisticsMap.put(mapKeyByFound, foundPlayerStatistic);
            }

            //특정 리그의 모든 팀 조회
            for (Long teamId : teamIds) {

                checkApiCount();

                // 페이징 길이 조회
                Map<String, Object> map = RestClient.create().get()
                        .uri("https://api-football-v1.p.rapidapi.com/v3/players?season={season}&league={leagueId}&team={teamId}", season, leagueId, teamId)
                        .header("x-rapidapi-host", "api-football-v1.p.rapidapi.com")
                        .header("x-rapidapi-key", rapidApiKey)
                        .retrieve()
                        .body(new ParameterizedTypeReference<>() {
                        });
                ++apiCount;

                Map<String, Object> pagingMap = (Map<String, Object>) map.get("paging");
                int pagingLength = (Integer) pagingMap.get("total");

                for (int i = 1; i <= pagingLength; i++) {
                    // api 호출 제한때문에 조절
                    checkApiCount();

                    Map<String, Object> rootMap = RestClient.create().get()
                            .uri("https://api-football-v1.p.rapidapi.com/v3/players?season={season}&league={leagueId}&team={teamId}&page={page}", season, leagueId, teamId, i)
                            .header("x-rapidapi-host", "api-football-v1.p.rapidapi.com")
                            .header("x-rapidapi-key", rapidApiKey)
                            .retrieve()
                            .body(new ParameterizedTypeReference<>() {
                            });
                    ++apiCount;

                    List<Map<String, Object>> responseMaps = (List<Map<String, Object>>) rootMap.get("response");

                    // 2023년도에 진출했던 팀이 2024년도엔 진출 못 한 경우가 있음
                    if (responseMaps.isEmpty()) continue;

                    for (Map<String, Object> responseMap : responseMaps) {
                        Map<String, Object> playerMap = (Map<String, Object>) responseMap.get("player");

                        // player 초기화
                        Player player = objectMapper.convertValue(playerMap, Player.class);

                        if (existedPlayerIds.contains(player.getId())) continue;
                        //다음 반복문 STEP의 IF에 안걸리게 리스트에 추가
                        existedPlayerIds.add(player.getId());

                        //외부 api를 내 db에 맞게 수정
                        String firstName = (String) playerMap.get("firstname");
                        String lastName = (String) playerMap.get("lastname");
                        player.addName(firstName, lastName);

                        newPlayers.add(player);

                        // 이적한 선수는 통계를  두개 이상 가질 가능성이 있어서 map's' 입니다.
                        List<Map<String, Object>> statisticsMaps = (List<Map<String, Object>>) responseMap.get("statistics");
                        for (Map<String, Object> statisticsMap : statisticsMaps) {
                            Map<String, Object> teamMap = (Map<String, Object>) statisticsMap.get("team");
                            Long teamIdByTeamMap = Long.valueOf((Integer) teamMap.get("id"));
                            Team team = new Team(teamIdByTeamMap);

                            Map<String, Object> leagueMap = (Map<String, Object>) statisticsMap.get("league");
                            Long leagueIdByLeagueMap = Long.valueOf((Integer) leagueMap.get("id"));
                            League league = new League(leagueIdByLeagueMap);
                            int seasonByByLeagueMap = (int) leagueMap.get("season");

                            Map<String, Object> gamesMap = (Map<String, Object>) statisticsMap.get("games");
                            String position = (String) gamesMap.get("position");

                            Double rating = (double) 0;
                            Object objectRating = gamesMap.get("rating");
                            if (objectRating != null) rating = Double.parseDouble((String) objectRating);

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

                            PlayerStatistics newPlayerStatistics = PlayerStatistics.builder().player(player).team(team)
                                    .league(league).position(position).rating(rating).shots(shots).goals(goals).passes(passes)
                                    .tackles(tacklesTotal).dribbles(dribbles).fouls(fouls).cards(cards).season(seasonByByLeagueMap)
                                    .build();

                            StringBuilder stringBuilder = new StringBuilder();
                            String mapKeyByRapidApi = stringBuilder.append(player.getId()).append("-").append(teamIdByTeamMap).append("-").append(leagueIdByLeagueMap).append("-").append(seasonByByLeagueMap).toString();
                            PlayerStatistics existing = foundPlayerStatisticsMap.get(mapKeyByRapidApi);

                            //이미 존재하는 선수 통계면 변경감지로 UPDATE
                            if (existing != null)
                                existing.updatePlayerStatistics(rating, shots, goals, passes, tacklesTotal, dribbles, fouls, cards);
                            else
                                newPlayerStatisticsList.add(newPlayerStatistics);
                        }
                    }
                }
            }

            jdbcTemplateRepository.savePlayers(newPlayers);
            jdbcTemplateRepository.savePlayerStatistics(newPlayerStatisticsList);
            log.info("updatePlayerStatistics 스케줄 완료");
        }

        public List<Player> searchPlayerProfiles(List<Long> playerIds) throws InterruptedException {
            List<Player> players = new ArrayList<>();
            for (Long playerId : playerIds) {

                checkApiCount();

                Map<String, Object> map = RestClient.create().get()
                        .uri("https://api-football-v1.p.rapidapi.com/v3/players/profiles?player={playerId}", playerId)
                        .header("x-rapidapi-host", "api-football-v1.p.rapidapi.com")
                        .header("x-rapidapi-key", rapidApiKey)
                        .retrieve()
                        .body(new ParameterizedTypeReference<>() {
                        });

                ++apiCount;

                List<Map<String, Object>> responseMaps = (List<Map<String, Object>>) map.get("response");

                for (Map<String, Object> responseMap : responseMaps) {
                    Map<String, Object> playerMap = (Map<String, Object>) responseMap.get("player");


                    Player player = objectMapper.convertValue(playerMap, Player.class);
                    String firstName = (String) playerMap.get("firstname");
                    String lastName = (String) playerMap.get("lastname");
                    player.addName(firstName, lastName);
                    players.add(player);
                }
            }
            return players;
        }

        // Integer에 null이 들어오면 기본값 0을 넣어주는 함수
        public int invokeIntegerOrElse(Object objectValue) {
            Integer integerValue = (Integer) objectValue;

            if (integerValue == null) return 0;
            else return integerValue;
        }

        // Integer에 null이 들어오면 기본값 0을 넣어주는 함수
        public Double invokeDoubleOrElse(Double doubleValue) {
            if (doubleValue == null) return (double) 0;
            else return doubleValue;
        }
    }

}
