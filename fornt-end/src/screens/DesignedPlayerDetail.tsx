import React, { useState, useEffect } from "react";
import {
  Card,
  CardMedia,
  CardContent,
  Typography,
  CircularProgress,
  Grid,
  Divider,
  Box,
  Container,
  useMediaQuery,
} from "@mui/material";
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from "recharts";
import MyHeader from "../my-component/MyHeader";
import MyFooter from "../my-component/MyFooter";
import { useParams, useSearchParams } from "react-router-dom";
import MyComment from "../my-component/MyComment";

interface PlayerProfileProps {
  keyword: string;
}

interface Player {
  information: {
    name: string;
    age: number;
    birthDate: string;
    nationality: string;
    height: string;
    weight: string;
    photo: string;
  };
  stats: {
    passes: number;
    shots: number;
    saves: number;
    tackles: number;
    dribbles: number;
    foulDrawn: number;
    foulCommitted: number;
  };
  records: {
    leagueSeason: number; // assuming this is the current season year
    leagueName: string;
    position: string;
    goals: number;
    assists: number;
    yellowCard: number;
    redCard: number;
  };
}

const PlayerProfile: React.FC = () => {
  const [player, setPlayer] = useState<Player | null>(null);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const { keyword } = useParams<{ keyword: string }>();
  const [searchParams] = useSearchParams();
  const [leagueSeason, setLeagueSeason] = useState<number>(
    parseInt(searchParams.get("leagueSeason") ?? "2024")
  );

  const fetchPlayerData = async () => {
    setLoading(true);
    setError(null);

    try {
      const response = await fetch(
        `https://dldm.kr/api/players/${keyword}?leagueSeason=${leagueSeason}`
      );
      //const response = await fetch(`http://localhost:8080/api/players/${keyword}?leagueSeason=${leagueSeason}`);
      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(
          `선수 정보를 불러오는 데 실패했습니다. 상태: ${response.status}, 메시지: ${errorData.message}`
        );
      }
      const data = await response.json();
      console.log(data)
      if (data?.statistics.leagueName == null){
         setLeagueSeason(2023);
      }
      else {
        setPlayer({
          information: {
            name: data.information?.name || "알 수 없음",
            age: data.information?.age || 0,
            birthDate: data.information?.birthDate || "정보 없음",
            nationality: data.information?.nationality || "정보 없음",
            height: data.information?.height || "정보 없음",
            weight: data.information?.weight || "정보 없음",
            photo: data.information?.photo || "default_image_url",
          },
          stats: {
            passes: data.statistics?.graph.passes || 0,
            shots: data.statistics?.graph.shots || 0,
            saves: data.statistics?.graph.saves || 0,
            tackles: data.statistics?.tackles || 0,
            dribbles: data.statistics?.dribbles || 0,
            foulDrawn: data.statistics?.foulDrawn || 0,
            foulCommitted: data.statistics?.foulCommitted || 0,
          },
          records: {
            leagueSeason: data.statistics?.leagueSeason || 0,
            leagueName: data.statistics?.leagueName || "정보 없음",
            position: data.statistics?.position || "정보 없음",
            goals: data.statistics?.goals || 0,
            assists: data.statistics?.graph.assists || 0,
            yellowCard: data.statistics?.yellowCard || 0,
            redCard: data.statistics?.redCard || 0,
          },
        });
      }
    } catch (err: any) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };
  const isMobile = useMediaQuery("(max-width:600px)"); // 훅을 최상위에서 호출

  useEffect(() => {
    fetchPlayerData();
  }, [keyword, leagueSeason]);

  if (loading) {
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
  }

  if (error) {
    return (
      <Typography color="error" variant="h6" align="center" mt={4}>
        오류: {error}
      </Typography>
    );
  }

  if (!player) {
    return (
      <Typography variant="h6" align="center" mt={4}>
        선수 데이터를 찾을 수 없습니다.
      </Typography>
    );
  }

  // Bar graph data
  const barData = [
    { name: "패스", value: player.stats.passes },
    { name: "슈팅", value: player.stats.shots },
    { name: "세이브", value: player.stats.saves },
    { name: "태클", value: player.stats.tackles },
    { name: "드리블", value: player.stats.dribbles },
    { name: "파울 유도", value: player.stats.foulDrawn },
  ];

  return (
    <Box sx={{ display: "flex", flexDirection: "column", minHeight: "100vh" }}>
      <MyHeader />
      <Container>
        <Card sx={{ maxWidth: "92%", mx: "auto", mt: 4, boxShadow: 3 }}>
          <CardContent>
            <Grid container spacing={3}>
              {/* Left Section: Player Bio */}
              <Grid item xs={12} md={5}>
                <Box
                  sx={{
                    position: "relative",
                    display: "flex",
                    flexDirection: "column",
                    alignItems: "center",
                  }}
                >
                  <CardMedia
                    component="img"
                    image={player.information.photo}
                    alt={player.information.name}
                    sx={{
                      width: "150px",
                      height: "150px",
                      borderRadius: "50%",
                      objectFit: "cover",
                      marginBottom: 2,
                    }}
                  />

                  <Typography
                    variant="h5"
                    gutterBottom
                    sx={{ marginTop: 2, textAlign: "center" }}
                  >
                    {player.information.name}
                  </Typography>

                  <Box sx={{ textAlign: "center", marginTop: 1 }}>
                    <Typography variant="body2">
                      나이: {player.information.age}
                    </Typography>
                    <Typography variant="body2">
                      생년월일: {player.information.birthDate}
                    </Typography>
                    <Typography variant="body2">
                      국적: {player.information.nationality}
                    </Typography>
                    <Typography variant="body2">
                      키: {player.information.height}
                    </Typography>
                    <Typography variant="body2">
                      몸무게: {player.information.weight}
                    </Typography>
                  </Box>
                </Box>
              </Grid>

              {/* Right Section: Player Stats */}

              <Grid item xs={12} md={7}>
                <Box sx={{ mt: 3 }}>
                  <ResponsiveContainer width="100%" height={300}>
                    <BarChart data={barData}>
                      <CartesianGrid strokeDasharray="3 3" />
                      <XAxis
                        dataKey="name"
                        interval={0}
                        angle={isMobile ? -30 : 0} // 모바일에서만 -30도로 회전
                        textAnchor={isMobile ? "end" : "middle"} // 모바일에서만 끝으로 설정
                        tickMargin={10}
                      />
                      <YAxis />
                      <Tooltip />
                      <Legend />
                      <Bar dataKey="value" fill="#007bff" />
                    </BarChart>
                  </ResponsiveContainer>
                </Box>
              </Grid>
            </Grid>

            <Divider sx={{ my: 3 }} />

            {/* Bottom Section: Player Records */}
            <Box
              sx={{
                border: "1px solid #ccc",
                borderRadius: 2,
                padding: 2,
                backgroundColor: "#f9f9f9",
              }}
            >
              <Grid container spacing={2} mb={2}>
                <Grid item xs={6}>
                  <Box>
                    <Typography variant="body2">
                      {player.records.leagueSeason} 시즌
                      {/* Season distinction */}
                    </Typography>
                    <Typography variant="body2">
                      포지션: {player.records.position}
                    </Typography>
                    <Typography variant="body2">
                      {player.records.leagueName}
                    </Typography>
                  </Box>
                </Grid>
                <Grid item xs={6}>
                  <Box>
                    <Typography variant="body2">
                      골: {player.records.goals} / 어시스트:{" "}
                      {player.records.assists}
                    </Typography>
                  </Box>
                </Grid>
              </Grid>

              <Grid container spacing={2}>
                <Typography
                  variant="body1"
                  flex="0 1 150px"
                  textAlign="right"
                  display="flex"
                  alignItems="center"
                  justifyContent="flex-end"
                >
                  <span
                    style={{
                      display: "inline-block",
                      backgroundColor: "red",
                      width: "18px",
                      height: "25px",
                      marginRight: "8px",
                      borderRadius: "4px", // 둥근 모서리로 레드 카드
                    }}
                  />
                  : {player.records.redCard}
                  <span style={{ marginRight: "16px" }} />
                  <span
                    style={{
                      display: "inline-block",
                      backgroundColor: "#ffeb3b",
                      width: "18px",
                      height: "25px",
                      marginRight: "8px",
                      borderRadius: "4px", // 둥근 모서리로 옐로우 카드
                    }}
                  />
                  : {player.records.yellowCard}
                </Typography>
              </Grid>
            </Box>
          </CardContent>
        </Card>
      </Container>

      {/* 댓글 목록, 페이징, 댓글 등록 */}
      <Container
        style={{ marginTop: "20px", textAlign: "left", marginBottom: "20px" }}
      >
        <MyComment category="players" />
      </Container>
      <MyFooter />
    </Box>
  );
};

export default PlayerProfile;
