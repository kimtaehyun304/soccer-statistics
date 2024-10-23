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
  Typography,
  Button,
  Box,
  FormControl,
  InputLabel,
  MenuItem,
  Select,
  SelectChangeEvent,
} from "@mui/material";
import { useNavigate } from "react-router-dom";
import MyHeader from "../my-component/MyHeader";
import { useMediaQuery } from "@mui/material";
import MyFooter from "../my-component/MyFooter";
import LeagueSelect from "../my-component/LeagueSelect";

interface PlayerRankingDto {
  ranking: number;
  photo: string;
  name: string;
  position: string;
  rating: number;
}

const PlayerRanking: React.FC = () => {
  const [players, setPlayers] = useState<PlayerRankingDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [leagueName, setLeagueName] = useState("Premier League");
  const [leagueSeason, setLeagueSeason] = useState(2024);
  const navigate = useNavigate();

  // Detect if the screen size is small (mobile)
  const isMobile = useMediaQuery("(max-width:600px)");

  const handleLeagueChange = (
    event: SelectChangeEvent<string>,
    child: ReactNode
  ) => {
    const newLeague = event.target.value as string;
    setLeagueName(newLeague);
    //navigate(`/teams/ranking?leagueName=${newLeague}&leagueSeason=2023`); // Update the URL
  };

  const handleSeasonChange = (
    event: SelectChangeEvent<number>,
    child: ReactNode
  ) => {
    const newSeason = event.target.value as number;
    setLeagueSeason(newSeason);
    //navigate(`/teams/ranking?leagueName=${newLeague}&leagueSeason=2023`); // Update the URL
  };

  useEffect(() => {
    const fetchPlayers = async () => {
      try {
        const response = await fetch(
          `https://dldm.kr/api/ranking/players?leagueName=${leagueName}&leagueSeason=${leagueSeason}`
        );
        //const response = await fetch(`http://localhost:8080/api/ranking/players?leagueName=${leagueName}&leagueSeason=2023`);
        if (!response.ok) {
          throw new Error(await response.text());
        }
        const data: PlayerRankingDto[] = await response.json();
        setPlayers(data);
        setError(null)
      } catch (err: any) {
        setError(err.message);
        setPlayers([]);
      } finally {
        setLoading(false);
      }
    };

    fetchPlayers();
  }, [leagueName, leagueSeason]);

  if (loading)
    return (
      <Box
        sx={{ display: "flex", flexDirection: "column", minHeight: "100vh" }}
      >
        <MyHeader />
        <Container sx={{ marginBottom: "15px" }}>
          <LeagueSelect
            category="선수"
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

  const handleRowClick = (playerName: string) => {
    navigate(`/players/${playerName}?leagueName=${leagueName}&leagueSeason=${leagueSeason}`);
  };

  return (
    <Box sx={{ display: "flex", flexDirection: "column", minHeight: "100vh" }}>
      <MyHeader />
      <Container>
        <LeagueSelect
          category="선수"
          leagueName={leagueName}
          leagueSeason={leagueSeason}
          onLeagueChange={handleLeagueChange}
          onSeasonChange={handleSeasonChange}
        />


        <div style={{ overflowX: "auto" }}>
          <TableContainer component={Paper} sx={{ marginBottom: "15px" }}>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>랭킹</TableCell>
                  <TableCell>사진</TableCell>
                  <TableCell>이름</TableCell>
                  {!isMobile && <TableCell>Position</TableCell>}{" "}
                  {/* Hide on mobile */}
                  {!isMobile && <TableCell>ratings</TableCell>}{" "}
                  {/* Hide on mobile */}
                </TableRow>
              </TableHead>
              <TableBody>
                {players.map((player, index) => (
                  <TableRow
                    key={index}
                    hover
                    onClick={() => handleRowClick(player.name)}
                    style={{ cursor: "pointer" }}
                  >
                    <TableCell>{player.ranking}</TableCell>
                    <TableCell>
                      <img
                        src={player.photo}
                        alt={player.name}
                        style={{
                          width: "40px",
                          height: "40px",
                          borderRadius: "50%",
                        }}
                      />
                    </TableCell>
                    <TableCell>{player.name}</TableCell>
                    {!isMobile && <TableCell>{player.position}</TableCell>}{" "}
                    {/* Hide on mobile */}
                    {!isMobile && <TableCell>{player.rating}</TableCell>}{" "}
                    {/* Hide on mobile */}
                  </TableRow>
                ))}
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

export default PlayerRanking;
