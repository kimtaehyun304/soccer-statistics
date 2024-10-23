import React, { useEffect, useState } from "react";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import {
  Box,
  Typography,
  Avatar,
  Container,
  Paper,
  Grid,
  Switch,
  Button,
  Pagination,
  TextField,
} from "@mui/material";
import MyHeader from "../my-component/MyHeader";
import MyFooter from "../my-component/MyFooter";
import MyComment from "../my-component/MyComment";

// DTO 정의
interface TeamInformationDto {
  teamName: string;
  logo: string;
}

interface TeamStatisticsDto {
  played: number;
  wins: number;
  losses: number;
  draws: number;
  goalTotal: number;
  againstTotal: number;
  yellowTotal: number;
  redTotal: number;
}

interface LineupDto {
  formation: string;
  played: number;
}

interface GameFixtureDto {
  referee: string;
  date: string;
  winnerTeamName: string | null;
  homeAway: string;
  team1: TeamDto;
  team2: TeamDto;
}

interface TeamDto {
  name: string;
  logo: string;
  goals: number;
}

interface SearchTeamDto {
  information: TeamInformationDto;
  leagueName: string;
  leagueSeason: number;
  statistics: TeamStatisticsDto;
  lineups: LineupDto[];
  gameFixtures: GameFixtureDto[];
  team1: TeamDto;
  team2: TeamDto;
}

const TeamDetail: React.FC = () => {
  const { keyword } = useParams<{ keyword: string }>();
  const [data, setData] = useState<SearchTeamDto | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [showFormations, setShowFormations] = useState(true); // Formations visibility state
  const [showFixtures, setShowFixtures] = useState(true); // Fixture visibility state

  // 팀 정보 및 통계 불러오기 함수
  const fetchTeamData = async () => {
    try {
      const response = await fetch(`https://dldm.kr/api/teams/${keyword}`);
      if (!response.ok) {
        throw new Error("Network response was not ok");
      }
      const result: SearchTeamDto = await response.json();
      setData(result);
    } catch (error: any) {
      setError(error.message);
    } finally {
      setLoading(false);
    }
  };

  // 팀 정보 및 통계
  useEffect(() => {
    if (keyword) {
      fetchTeamData();
    }
  }, [keyword]);


  if (loading)
    return (
      <Box
        sx={{ display: "flex", flexDirection: "column", minHeight: "100vh" }}
      >
        <MyHeader />
        <Box
          display="flex"
          flexDirection="column"
          justifyContent="center"
          alignItems="center"
          minHeight="100vh"
          textAlign="center"
          marginBottom="15px"
        >
          <Typography>Loading...</Typography>
        </Box>
        <MyFooter />
      </Box>
    );
  if (error) return <Typography>Error: {error}</Typography>;
  if (!data) return null;

  const { information, statistics, lineups, gameFixtures } = data;

  // 승패 결과에 따른 배경색 설정
  const getMatchResultColor = (fixture: GameFixtureDto) => {
    if (fixture.winnerTeamName === fixture.team1.name) return "lightblue"; // 팀 1 승리
    if (fixture.winnerTeamName === fixture.team2.name) return "lightcoral"; // 팀 2 승리
    return "lightgray"; // 무승부
  };

  // 포메이션 switch toggle
  const handleFormationsSwitchChange = (
    event: React.ChangeEvent<HTMLInputElement>
  ) => {
    setShowFormations(event.target.checked);
  };

  // 경기 전적 switch toggle
  const handleFixturesSwitchChange = (
    event: React.ChangeEvent<HTMLInputElement>
  ) => {
    setShowFixtures(event.target.checked);
  };

  return (
    <Box sx={{ display: "flex", flexDirection: "column", minHeight: "100vh" }}>
      <MyHeader />
      <Box
        display="flex"
        flexDirection="column"
        justifyContent="center"
        alignItems="center"
        minHeight="100vh"
        textAlign="center"
        marginBottom="15px"
      >
        <Avatar
          src={information.logo}
          alt={information.teamName}
          sx={{ width: 200, height: 200, margin: "20px auto" }}
        />
        <Container>
          <Typography variant="h4">{information.teamName}</Typography>
          <Typography variant="h6">
            League: {data.leagueName} {data.leagueSeason}
          </Typography>
        </Container>

        {/* 팀 통계*/}
        <Container style={{ marginTop: "20px" }}>
          <Typography variant="h5">팀 통계</Typography>
          <Typography>총 경기 수: {statistics.played}</Typography>
          <Typography>승: {statistics.wins}</Typography>
          <Typography>패: {statistics.losses}</Typography>
          <Typography>무승부: {statistics.draws}</Typography>
          <Typography>+Goals: {statistics.goalTotal}</Typography>
          <Typography>-Goals: {statistics.againstTotal}</Typography>
          <Typography>Yellow Cards: {statistics.yellowTotal}</Typography>
          <Typography>Red Cards: {statistics.redTotal}</Typography>
        </Container>

        {/* 포메이션*/}
        <Container style={{ marginTop: "20px" }}>
          <Box display="flex" alignItems="center" justifyContent="center">
            <Typography variant="h5">포메이션</Typography>
            <Switch
              checked={showFormations}
              onChange={handleFormationsSwitchChange}
            />
          </Box>
          {showFormations && (
            <Paper style={{ padding: "16px", marginTop: "10px" }}>
              <Grid container spacing={2}>
                {lineups.length > 0 ? (
                  lineups.map((lineup, index) => (
                    <Grid item key={index} xs={12} sm={6} md={4}>
                      <Box
                        style={{
                          padding: "8px",
                          border: "1px solid #ddd",
                          borderRadius: "4px",
                        }}
                      >
                        <Typography>Formation: {lineup.formation}</Typography>
                        <Typography>Played: {lineup.played}</Typography>
                      </Box>
                    </Grid>
                  ))
                ) : (
                  <Typography>No lineups available.</Typography>
                )}
              </Grid>
            </Paper>
          )}
        </Container>

        {/* 경기 전적*/}
        <Container style={{ marginTop: "20px" }}>
          <Box display="flex" alignItems="center" justifyContent="center">
            <Typography variant="h5" style={{ marginRight: "10px" }}>
              경기 전적
            </Typography>
            <Switch
              checked={showFixtures}
              onChange={handleFixturesSwitchChange}
            />
          </Box>
          {showFixtures && (
            <Paper style={{ padding: "16px", marginTop: "10px" }}>
              {gameFixtures.length > 0 ? (
                gameFixtures.map((fixture, index) => (
                  <Box
                    key={index}
                    style={{
                      padding: "8px",
                      borderBottom: "1px solid #ddd",
                      backgroundColor: getMatchResultColor(fixture),
                    }}
                  >
                    <Typography>심판: {fixture.referee}</Typography>
                    <Typography>경기일: {fixture.date}</Typography>
                    <Typography>
                      Goals: {fixture.team1.goals} : {fixture.team2.goals}
                    </Typography>
                    <Box
                      display="flex"
                      justifyContent="space-between"
                      alignItems="center"
                    >
                      <Box display="flex" alignItems="center">
                        <Avatar
                          src={fixture.team1.logo}
                          alt={fixture.team1.name}
                          sx={{ width: 50, height: 50, marginRight: "8px" }}
                        />
                        <Typography>
                          {fixture.team1.name}: {fixture.team1.goals}
                        </Typography>
                      </Box>
                      <Box display="flex" alignItems="center">
                        <Avatar
                          src={fixture.team2.logo}
                          alt={fixture.team2.name}
                          sx={{ width: 50, height: 50, marginRight: "8px" }}
                        />
                        <Typography>
                          {fixture.team2.name}: {fixture.team2.goals}
                        </Typography>
                      </Box>
                    </Box>
                    <Typography>
                      승리:{" "}
                      {fixture.winnerTeamName ? fixture.winnerTeamName : ""}
                    </Typography>
                    <Typography>Home/Away: {fixture.homeAway}</Typography>
                  </Box>
                ))
              ) : (
                <Typography>No game fixtures available.</Typography>
              )}
            </Paper>
          )}
        </Container>

        {/* 댓글 목록, 페이징, 댓글 등록*/}
        <Container style={{ marginTop: "20px", textAlign: "left" }}>
              <MyComment category="teams"/>
        </Container>
        
      </Box>
      <MyFooter />
    </Box>
  );
};

export default TeamDetail;
