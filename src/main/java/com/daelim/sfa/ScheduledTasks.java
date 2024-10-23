package com.daelim.sfa;

import com.daelim.sfa.domain.League;
import com.daelim.sfa.domain.News;
import com.daelim.sfa.domain.game.GameFixture;
import com.daelim.sfa.domain.player.*;
import com.daelim.sfa.domain.team.*;
import com.daelim.sfa.dto.NewsDto;
import com.daelim.sfa.repository.GameFixtureRepository;
import com.daelim.sfa.repository.InitRepository;
import com.daelim.sfa.repository.MemberRepository;
import com.daelim.sfa.repository.NewsRepository;
import com.daelim.sfa.repository.player.PlayerRepository;
import com.daelim.sfa.repository.player.PlayerStatisticsRepository;
import com.daelim.sfa.repository.team.LineupRepository;
import com.daelim.sfa.repository.team.TeamRepository;
import com.daelim.sfa.repository.team.TeamStatisticsRepository;
import com.daelim.sfa.service.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import javax.sound.sampled.Line;
import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class ScheduledTasks {

    private final NewsRepository newsRepository;
    private final NewsService newsService;
    private final GameFixtureRepository gameFixtureRepository;
    private final GameFixtureService gameFixtureService;
    private final TeamStatisticsRepository teamStatisticsRepository;
    private final TeamStatisticsService teamStatisticsService;
    private final ObjectMapper objectMapper;
    private final LineupRepository lineupRepository;
    private final LineupService lineupService;
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final InitRepository initRepository;
    private final PlayerStatisticsRepository playerStatisticsRepository;
    private final PlayerStatisticsService playerStatisticsService;

    @Value("${rapidApiKey}")
    private String rapidApiKey;

    // 연합 뉴스 크롤링
    @Scheduled(cron = "0 0 13 ? * *", zone = "Asia/Seoul")
    public void saveCrawlingNews() {
        //log.info("saveCrawlingNews 메서드 실행");
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
                if(createdAt.isEmpty())
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

                if(newsList.size() == 2)
                    break;
            }

            newsService.saveNewsList(newsList);
            //log.info("saveCrawlingNews 메서드 종료");
        } catch (IOException e) {
            log.error("크롤링 실패");
        }
    }

    @Scheduled(cron = "0 0 23 * * ?", zone = "Europe/London")
    public void update() {
        Long leagueId = 39L;
        int season = 2024;

        ZonedDateTime londonTime = ZonedDateTime.now(ZoneId.of("Europe/London"));
        LocalDate todayLondon = londonTime.toLocalDate();

        List<GameFixture> foundGameFixtures = gameFixtureRepository.findAllByDateAndLeagueIdAndSeason(todayLondon, leagueId, season);
        System.out.println("foundGameFixtures.size() = " + foundGameFixtures.size());

        if (foundGameFixtures.isEmpty())
            return;

        updateFinishedGameFixtures(foundGameFixtures, leagueId, season, todayLondon);
        updateTeamStatisticsAndLineup(foundGameFixtures, leagueId, season);
        updatePlayerStatistics(foundGameFixtures, leagueId, season);
    }

    private void updateFinishedGameFixtures(List<GameFixture> foundGameFixtures, Long leagueId, int season, LocalDate todayLondon) {
        //log.info("updateFinishedGameFixtures 메서드 실행");
        //List<GameFixture> gameFixturesByRapidApi = new ArrayList<>();
        Map<Long, GameFixture> gameFixtureMapByRapidApi = new HashMap<>();


        League league = new League(leagueId);

        Map<String, Object> map = RestClient.create().get()
                .uri("https://api-football-v1.p.rapidapi.com/v3/fixtures?league={leagueId}&season={season}&date={date}", leagueId, season, todayLondon)
                .header("x-rapidapi-host", "api-football-v1.p.rapidapi.com")
                .header("x-rapidapi-key", rapidApiKey)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        List<Map<String, Object>> responseMaps = (List<Map<String, Object>>) map.get("response");
        for (Map<String, Object> responseMap : responseMaps) {
            Map<String, Object> fixtureMap = (Map<String, Object>) responseMap.get("fixture");

            Long fixtureId = Long.valueOf(((Integer) fixtureMap.get("id")));
            String referee = (String) fixtureMap.get("referee");
            String timezone = (String) fixtureMap.get("timezone");
            LocalDate date = OffsetDateTime.parse((String) fixtureMap.get("date")).toLocalDate();

            Integer intVenueId = ((Integer) ((Map<String, Object>) fixtureMap.get("venue")).get("id"));

            Long venueId = null;
            if (intVenueId != null) venueId = Long.valueOf(intVenueId);

            Venue venue = new Venue(venueId);

            Map<String, Object> teamsMap = (Map<String, Object>) responseMap.get("teams");

            Map<String, Object> homeMap = (Map<String, Object>) teamsMap.get("home");
            Map<String, Object> awayMap = (Map<String, Object>) teamsMap.get("away");

            Map<String, Object> goalsMap = (Map<String, Object>) responseMap.get("goals");


            Team homeTeam = new Team(Long.valueOf((Integer) homeMap.get("id")));

            Team team1 = new Team(Long.valueOf((Integer) homeMap.get("id")));
            int team1Goals = (int) goalsMap.get("home");

            Team team2 = new Team(Long.valueOf((Integer) awayMap.get("id")));
            int team2Goals = (int) goalsMap.get("away");

            Boolean team1Win = (Boolean) homeMap.get("winner");
            Long winnerTeamId = null;

            if (team1Win != null)
                winnerTeamId = team1Win ? team1.getId() : team2.getId();

            Team winnerTeam = new Team(winnerTeamId);

            GameFixture gameFixture = GameFixture.builder().id(fixtureId).referee(referee).timezone(timezone).date(date).venue(venue).homeTeam(homeTeam).league(league).season(season).team1(team1).team2(team2).team1Goals(team1Goals).team2Goals(team2Goals).winnerTeam(winnerTeam).build();
            gameFixtureMapByRapidApi.put(gameFixture.getId(), gameFixture);
            //gameFixturesByRapidApi.add(gameFixture);
        }

        // 변경감지 로직
        for (GameFixture foundGameFixture : foundGameFixtures) {
            GameFixture gameFixture = gameFixtureMapByRapidApi.get(foundGameFixture.getId());
            String referee = gameFixture.getReferee();
            Team winnerTeam = gameFixture.getWinnerTeam();
            int team1Goals = gameFixture.getTeam1Goals();
            int team2Goals = gameFixture.getTeam2Goals();

            // 이렇게 하니까 변경감지가 안됨 (em을 공유하면 안되나?)
            //gameFixtureService.updateFinishedGame(foundGameFixture, referee, winnerTeam, team1Goals, team2Goals);
            gameFixtureService.updateFinishedGame(foundGameFixture.getId(), referee, winnerTeam, team1Goals, team2Goals);

        }

        //log.info("updateFinishedGameFixtures 메서드 종료");
    }

    private void updateTeamStatisticsAndLineup(List<GameFixture> foundGameFixtures, Long leagueId, int season) {

       // log.info("updateTeamStatisticsAndLineup 메서드 실행");

        List<Long> teamIds = new ArrayList<>();

        // 오늘 경기한 팀 리스트 생성
        for (GameFixture foundGameFixture : foundGameFixtures) {
            teamIds.add(foundGameFixture.getTeam1().getId());
            teamIds.add(foundGameFixture.getTeam2().getId());
        }

        List<TeamStatistics> foundTeamStatisticsList = teamStatisticsRepository.findAllWithLineUpByLeagueIdAndSeasonInTeamId(leagueId, season, teamIds);

        // key: teamId
        Map<Long, TeamStatistics> foundTeamStatisticsMap = new HashMap<>();
        for (TeamStatistics foundTeamStatistics : foundTeamStatisticsList) {
            foundTeamStatisticsMap.put(foundTeamStatistics.getTeam().getId(), foundTeamStatistics);
        }


        //for (TeamStatistics foundTeamStatistics : foundTeamStatisticsList) {
        for (Long teamId : teamIds) {
            //Long teamId = foundTeamStatistics.getTeam().getId();
            Map<String, Object> map = RestClient.create().get()
                    .uri("https://api-football-v1.p.rapidapi.com/v3/teams/statistics?league={leagueId}&team={teamId}&season={season}", leagueId, teamId, season)
                    .header("x-rapidapi-host", "api-football-v1.p.rapidapi.com")
                    .header("x-rapidapi-key", rapidApiKey)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });

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
            TeamStatistics teamStatisticsByRapidApi = TeamStatistics.builder().team(team).league(league).fixtures(fixtures).goals(goals).cards(cards).season(season).build();

            TeamStatistics foundTeamStatistics = foundTeamStatisticsMap.get(teamId);

            teamStatisticsService.updateTeamStatistics(foundTeamStatistics.getId(), teamStatisticsByRapidApi.getFixtures(), teamStatisticsByRapidApi.getGoals(), teamStatisticsByRapidApi.getCards());

            //lineups
            Map<String, Lineup> foundLineupMap = new HashMap<>();
            List<Lineup> foundLineups = foundTeamStatistics.getLineups();
            for (Lineup foundLineup : foundLineups) {
                foundLineupMap.put(foundLineup.getFormation(), foundLineup);
            }

            // 새로운 포메이션이면 저장, played 수가 늘면 변경감지
            List<Lineup> lineupsByRapidApi = objectMapper.convertValue(responseMap.get("lineups"), new TypeReference<>() {});
            for (Lineup lineupByRapidApi : lineupsByRapidApi) {
                if (!foundLineupMap.containsKey(lineupByRapidApi.getFormation())) {
                    lineupByRapidApi.addTeamStatistics(foundTeamStatistics);
                    lineupService.save(lineupByRapidApi);
                    continue;
                }

                Lineup foundLineup = foundLineupMap.get(lineupByRapidApi.getFormation());
                lineupService.updateLineup(foundLineup.getId(), lineupByRapidApi.getPlayed());
            }
        }
        //log.info("updateTeamStatisticsAndLineup 메서드 완료");

    }

    private void updatePlayerStatistics(List<GameFixture> foundGameFixtures, Long leagueId, int season)  {
        // log.info("updatePlayerStatistics 메서드 실행");
        List<Long> teamIds = new ArrayList<>();

        // 오늘 경기한 팀 리스트 생성
        for (GameFixture foundGameFixture : foundGameFixtures) {
            teamIds.add(foundGameFixture.getTeam1().getId());
            teamIds.add(foundGameFixture.getTeam2().getId());
        }

        List<Player> players = new ArrayList<>();
        List<Player> foundPlayers = playerRepository.findAll();

        Set<Long> foundPlayerIds = new HashSet<>(foundPlayers.stream().map(Player::getId).toList());
        List<PlayerStatistics> newPlayerStatisticsList = new ArrayList<>();

        List<PlayerStatistics> foundPlayerStatisticsList = playerStatisticsRepository.findAllWithLeagueByLeagueIdAndSeason(leagueId, season);

        // key: player_id-team_id-league_id-season
        Map<String, PlayerStatistics> foundPlayerStatisticsMap = new HashMap<>();
        for (PlayerStatistics foundPlayerStatistic : foundPlayerStatisticsList) {
            Long playerId = foundPlayerStatistic.getPlayer().getId();
            Long teamId = foundPlayerStatistic.getTeam().getId();
            StringBuilder stringBuilder = new StringBuilder();
            String mapKeyByFound = stringBuilder.append(playerId).append("-").append(teamId).append("-").append(leagueId).append("-").append(season).toString();
            foundPlayerStatisticsMap.put(mapKeyByFound, foundPlayerStatistic);
        }

        int apiCount = 0;

        //특정 리그의 모든 팀 조회
        for (Long teamId : teamIds) {
            // 페이징 전체 길이 조회
            Map<String, Object> map = RestClient.create().get()
                    .uri("https://api-football-v1.p.rapidapi.com/v3/players?season={season}&league={leagueId}&team={teamId}", season, leagueId, teamId)
                    .header("x-rapidapi-host", "api-football-v1.p.rapidapi.com")
                    .header("x-rapidapi-key", rapidApiKey)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });

            apiCount++;

            Map<String, Object> pagingMap = (Map<String, Object>) map.get("paging");
            int pagingTotal = (Integer) pagingMap.get("total");


            for (int i = 1; i <= pagingTotal; i++) {

                Map<String, Object> imap = RestClient.create().get()
                        .uri("https://api-football-v1.p.rapidapi.com/v3/players?season={season}&league={leagueId}&team={teamId}&page={page}", season, leagueId, teamId, i)
                        .header("x-rapidapi-host", "api-football-v1.p.rapidapi.com")
                        .header("x-rapidapi-key", rapidApiKey)
                        .retrieve()
                        .body(new ParameterizedTypeReference<>() {
                        });

                apiCount++;
                // log.info("current apiCount : {}", apiCount);
                try {
                    if (apiCount >= 30) {
                        // log.info("API 쿼타 제한 때문에 60초 대기합니다");
                        Thread.sleep(1000 * 61); // 60초 대기
                        apiCount = 0;
                    }
                } catch (InterruptedException e) {
                    log.error(e.getMessage());
                }

                List<Map<String, Object>> responseMaps = (List<Map<String, Object>>) imap.get("response");

                // 2023년도에 진출했던 팀이 2024년도엔 진출 못 한 경우가 있음
                if (responseMaps.isEmpty()) {
                    continue;
                }

                for (Map<String, Object> responseMap : responseMaps) {
                    Map<String, Object> playerMap = (Map<String, Object>) responseMap.get("player");

                    // player 초기화
                    Player player = objectMapper.convertValue(playerMap, Player.class);

                    if (foundPlayerIds.contains(player.getId())) {
                       // log.info("중복된 PK라 players에 안 넣습니다");
                        continue;
                    } else {
                        foundPlayerIds.add(player.getId());
                        String firstName = (String) playerMap.get("firstname");
                        String lastName = (String) playerMap.get("lastname");
                        player.addName(firstName, lastName);
                        players.add(player);
                    }

                    // 선수 정보 업데이트 로직 유기

                    // playerStatistics 초기화
                    // 이적 선수는 두개 이상 통계를 가질 가능성이 있습니다
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

                        // 새로운 선수 통계면 저장, 업데이트 되면 변경감지

                        PlayerStatistics playerStatisticsByRapidApi = PlayerStatistics.builder().player(player).team(team).league(league).position(position).rating(rating).shots(shots).goals(goals).passes(passes).tackles(tacklesTotal).dribbles(dribbles).fouls(fouls).cards(cards).season(seasonByByLeagueMap).build();

                        StringBuilder stringBuilder = new StringBuilder();
                        // key: player_id-team_id-league_id-season
                        String mapKeyByRapidApi = stringBuilder.append(player.getId()).append("-").append(teamIdByTeamMap).append("-").append(leagueIdByLeagueMap).append("-").append(seasonByByLeagueMap).toString();

                        // 데이터가 존재하면 변경감지, 없으면 insert
                        if(foundPlayerStatisticsMap.containsKey(mapKeyByRapidApi)) {
                            PlayerStatistics foundPlayerStatistics = foundPlayerStatisticsMap.get(mapKeyByRapidApi);
                            playerStatisticsService.updatePlayerStatistics(foundPlayerStatistics.getId(), rating, shots, goals, passes, tacklesTotal, dribbles, fouls, cards);
                        }
                        else
                            newPlayerStatisticsList.add(playerStatisticsByRapidApi);
                    }
                }
            }
        }
        initRepository.savePlayers(players);
        try {
            initRepository.savePlayerStatistics(newPlayerStatisticsList);
        } catch (DataIntegrityViolationException e) {
            // 중복 예외 발생 시 건너뛰기
            System.out.println("중복된 레코드가 존재하여 건너뜁니다: " + e.getMessage());
        }

        // .info("updatePlayerStatistics 메서드 완료");
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
