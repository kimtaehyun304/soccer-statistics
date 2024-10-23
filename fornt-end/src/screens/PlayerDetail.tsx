import React, { useEffect, useState } from "react";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  Legend,
  LabelList,
} from "recharts";  
import {
  Box,
  Typography,
  Avatar,
  Container,
  TextField,
  Button,
  Paper,
  Grid,
  Pagination,
} from "@mui/material";
import MyHeader from "../my-component/MyHeader";
import MyFooter from "../my-component/MyFooter";
import MyComment from "../my-component/MyComment";

interface PlayerData {
  information: {
    name: string;
    age: number;
    birthDate: string;
    birthCountry: string;
    nationality: string;
    height: string;
    weight: string;
    photo: string;
  };
  statistics: {
    leagueName: string;
    leagueSeason: number;
    position: string;
    graph: {
      passes: number;
      shots: number;
      assists: number;
      saves: number;
    };
    goals: number;
    goalsConceded: number;
    tackles: number;
    dribbles: number;
    foulDrawn: number;
    foulCommitted: number;
    yellowCard: number;
    redCard: number;
  };
}

const PlayerDetail: React.FC = () => {
  const { keyword } = useParams<{ keyword: string }>();
  const [data, setData] = useState<PlayerData | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  // Fetch player data
  useEffect(() => {
    const fetchPlayer = async () => {
      try {
        const response = await fetch(
          `https://dldm.kr/api/players/${keyword}`
        );
        if (!response.ok) {
          throw new Error("Network response was not ok");
        }
        const result: PlayerData = await response.json();
        setData(result);
      } catch (error: any) {
        setError(error.message);
      } finally {
        setLoading(false);
      }
    };

    if (keyword) {
      fetchPlayer();
    }
  }, [keyword]);

  if (loading)
    return (
      <Box
        sx={{ display: "flex", flexDirection: "column", minHeight: "100vh" }}
      >
        <MyHeader />
        <Container sx={{ marginBottom: "15px" }}>
          <Box
            display="flex"
            flexDirection="column"
            justifyContent="center"
            alignItems="center"
            minHeight="100vh"
            textAlign="center"
          >
            <Typography>Loading...</Typography>
          </Box>
        </Container>

        <MyFooter />
      </Box>
    );

  if (error) return <Typography>Error: {error}</Typography>;
  if (!data) return null;

  const { information, statistics } = data;

  const graphData = [
    { name: "Passes", value: statistics.graph.passes },
    { name: "Shots", value: statistics.graph.shots },
    { name: "Assists", value: statistics.graph.assists },
    { name: "Saves", value: statistics.graph.saves },
  ];

  return (
    <Box sx={{ display: "flex", flexDirection: "column", minHeight: "100vh" }}>
      <MyHeader />
      <Container sx={{ marginBottom: "15px" }}>
        <Box
          display="flex"
          flexDirection="column"
          justifyContent="center"
          alignItems="center"
          minHeight="100vh"
          textAlign="center"
        >
          <Avatar
            src={information.photo}
            alt={information.name}
            sx={{ width: 200, height: 200, margin: "20px auto" }}
          />
          <Container>
            <Typography variant="h4">{information.name}</Typography>
            <Typography>나이: {information.age}</Typography>
            <Typography>생년월일: {information.birthDate}</Typography>
            <Typography>출생 국가: {information.birthCountry}</Typography>
            <Typography>국적: {information.nationality}</Typography>
            <Typography>키: {information.height}</Typography>
            <Typography>체중: {information.weight}</Typography>
          </Container>

          <Container>
            <Grid container spacing={2} style={{ marginTop: "20px" }}>
              <Grid item xs={12} md={6}>
                <Typography variant="h5">
                  Statistics for {statistics.leagueName}{" "}
                  {statistics.leagueSeason}
                </Typography>
                <Typography>Position: {statistics.position}</Typography>
                <Typography>+Goals : {statistics.goals}</Typography>
                <Typography>-Goals- : {statistics.goalsConceded}</Typography>
                <Typography>태클: {statistics.tackles}</Typography>
                <Typography>드리블: {statistics.dribbles}</Typography>
                <Typography>당한 파울: {statistics.foulDrawn}</Typography>
                <Typography>시도한 파울: {statistics.foulCommitted}</Typography>
                <Typography>Yellow Cards: {statistics.yellowCard}</Typography>
                <Typography>Red Cards: {statistics.redCard}</Typography>
              </Grid>

              <Grid item xs={12} md={6}>
                <BarChart width={350} height={350} data={graphData}>
                  <XAxis dataKey="name" />
                  <YAxis />
                  <Tooltip />
                  <Legend />
                  <Bar dataKey="value" fill="#8884d8">
                    <LabelList dataKey="value" position="top" />
                  </Bar>
                </BarChart>
              </Grid>
            </Grid>
          </Container>

          {/* 댓글 목록, 페이징, 댓글 등록*/}
          <Container style={{ marginTop: "20px", textAlign: "left" }}>
            <MyComment category="players"/>
          </Container>
        </Box>
      </Container>

      <MyFooter />
    </Box>
  );
};

export default PlayerDetail;
