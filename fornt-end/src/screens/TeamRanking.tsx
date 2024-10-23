import React, { ReactNode, useEffect, useState } from "react";
import {
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Container,
  Box,
  SelectChangeEvent, // SelectChangeEvent를 가져옵니다.
} from "@mui/material";
import { useNavigate, useLocation } from "react-router-dom";
import MyHeader from "../my-component/MyHeader";
import { useMediaQuery } from "@mui/material";
import MyFooter from "../my-component/MyFooter";
import LeagueSelect from "../my-component/LeagueSelect";

interface TeamRankingDto {
  ranking: number;
  logo: string;
  name: string;
  goals: number;
  against: number;
  played: number;
  wins: number;
  losses: number;
  draws: number;
  points: number;
}

const TeamRanking: React.FC = () => {
  const [teams, setTeams] = useState<TeamRankingDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();
  const location = useLocation();
  const [leagueName, setLeagueName] = useState("Premier League");
  const [leagueSeason, setLeagueSeason] = useState(2024);
  const isMobile = useMediaQuery("(max-width:600px)");

  // SelectChangeEvent<string>를 사용하여 이벤트 타입을 맞춥니다.
  const handleLeagueChange = (
    event: SelectChangeEvent<string>, // 이벤트 타입을 SelectChangeEvent로 변경
    child: ReactNode
  ) => {
    const newLeague = event.target.value;
    setLeagueName(newLeague);
  };

  // SelectChangeEvent<number>를 사용하여 시즌 변경 이벤트 핸들링
  const handleSeasonChange = (
    event: SelectChangeEvent<number>, // 이벤트 타입을 SelectChangeEvent로 변경
    child: ReactNode
  ) => {
    const newSeason = Number(event.target.value); // number로 변환
    setLeagueSeason(newSeason);
  };

  useEffect(() => {
    const fetchTeams = async () => {
      try {
        const response = await fetch(
          `https://dldm.kr/api/ranking/teams?leagueName=${leagueName}&leagueSeason=${leagueSeason}`
        );
        if (!response.ok) {
          throw new Error(await response.text());
        }
        const data: TeamRankingDto[] = await response.json();
        setTeams(data);
        setError(null);
      } catch (err: any) {
        setError(err.message);
        setTeams([]);
      } finally {
        setLoading(false);
      }
    };

    fetchTeams();
  }, [leagueName, leagueSeason]);

  if (loading)
    return (
      <Box sx={{ display: "flex", flexDirection: "column", minHeight: "100vh" }}>
        <MyHeader />
        <Container sx={{ marginBottom: "15px" }}>
          <LeagueSelect
            category="팀"
            leagueName={leagueName}
            leagueSeason={leagueSeason}
            onLeagueChange={handleLeagueChange}
            onSeasonChange={handleSeasonChange}
          />
          <div>Loading...</div>
        </Container>
        <MyFooter />
      </Box>
    );

  const handleRowClick = (teamName: string) => {
    navigate(`/teams/${teamName}?leagueName=${leagueName}&leagueSeason=${leagueSeason}`);
  };

  return (
    <Box sx={{ display: "flex", flexDirection: "column", minHeight: "100vh" }}>
      <MyHeader />
      <Container sx={{ marginBottom: "15px" }}>
        <LeagueSelect
          category="팀"
          leagueName={leagueName}
          leagueSeason={leagueSeason}
          onLeagueChange={handleLeagueChange}
          onSeasonChange={handleSeasonChange}
        />

        <div style={{ overflowX: "auto"  }}>
          <TableContainer component={Paper}>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>랭킹</TableCell>
                  <TableCell>로고</TableCell>
                  <TableCell>이름</TableCell>
                   {/* 모바일이 아닌 경우에만 추가 열을 표시 */}
                   {!isMobile && <TableCell align="center">총 경기수</TableCell>}
                  {!isMobile && <TableCell align="center">승</TableCell>}
                  {!isMobile && <TableCell align="center">무</TableCell>}
                  {!isMobile && <TableCell align="center">패</TableCell>}
                  {!isMobile && <TableCell align="center">득점</TableCell>}
                  {!isMobile && <TableCell align="center">실점</TableCell>}
                  {!isMobile && <TableCell align="center">득실차</TableCell>}
                  {!isMobile && <TableCell align="center">승점</TableCell>}
                </TableRow>
              </TableHead>
              <TableBody>
                {teams.map((team, index) => {
                  const goalDifference = team.goals - team.against;
                  const points = team.wins * 3 + team.draws * 1;

                  return (
                    <TableRow
                      key={index}
                      hover
                      onClick={() => handleRowClick(team.name)}
                      style={{ cursor: "pointer" }}
                    >
                      <TableCell>{team.ranking}</TableCell>
                      <TableCell>
                        <img
                          src={team.logo}
                          alt={team.name}
                          style={{ width: "40px", height: "40px" }}
                        />
                      </TableCell>
                      <TableCell>{team.name}</TableCell>
                       {/* 모바일이 아닌 경우에만 추가 열을 표시 */}
                       {!isMobile && <TableCell align="center">{team.played}</TableCell>}
                      {!isMobile && <TableCell align="center">{team.wins}</TableCell>}
                      {!isMobile && <TableCell align="center">{team.draws}</TableCell>}
                      {!isMobile && <TableCell align="center">{team.losses}</TableCell>}
                      {!isMobile && <TableCell align="center">{team.goals}</TableCell>}
                      {!isMobile && <TableCell align="center">{team.against}</TableCell>}
                      {!isMobile && <TableCell align="center">{goalDifference}</TableCell>}
                      {!isMobile && <TableCell align="center">{points}</TableCell>}
                    </TableRow>
                  );
                })}
              </TableBody>
            </Table>
          </TableContainer>
        </div>

        {error ? error : ""}
      </Container>
      <MyFooter />
    </Box>
  );
};

export default TeamRanking;
