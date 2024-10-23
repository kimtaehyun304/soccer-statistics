import React, { useEffect, useState } from "react";
import {
  Card,
  CardContent,
  CardMedia,
  Typography,
  Grid,
  Box,
  Container,
  Link,
} from "@mui/material";
import MyHeader from "../my-component/MyHeader";
import MyFooter from "../my-component/MyFooter";
import useMediaQuery from '@mui/material/useMediaQuery';

interface News {
  id: number;
  title: string;
  content: string;
  createdAt: string;
  imageUrl: string;
  hrefUrl: string;
}

interface Team {
  name: string;
  logo: string;
}

interface GameFixture {
  date: string;
  team1: Team;
  team2: Team;
}

export default function Home() {
  const [newsData, setNewsData] = useState<News[]>([]);
  const [gameFixtureData, setGameFixtureData] = useState<GameFixture[]>([]);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const isMobile = useMediaQuery('(max-width:600px)'); // 모바일 화면 여부 확인
  useEffect(() => {
    const fetchNewsData = async () => {
      try {
        const response = await fetch(`https://dldm.kr/api/news`);
        const responseData = await response.json();
        setNewsData(responseData);
        setIsLoading(false);
      } catch (error) {
        setError("데이터를 불러오는 중 오류가 발생했습니다.");
        setIsLoading(false);
      }
    };

    fetchNewsData();
  }, []);

  useEffect(() => {
    const fetchGameFixtureData = async () => {
      try {
        const response = await fetch(`https://dldm.kr/api/gameFixtures`);
        const responseData = await response.json();
        setGameFixtureData(responseData);
        setIsLoading(false);
      } catch (error) {
        setError("데이터를 불러오는 중 오류가 발생했습니다.");
        setIsLoading(false);
      }
    };

    fetchGameFixtureData();
  }, []);

  if (isLoading) {
    return (
      <Box
        sx={{
          display: "flex",
          flexDirection: "column",
          minHeight: "100vh",
          backgroundColor: "#fafafa",
        }}
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

  if (error) return <Typography>Error: {error}</Typography>;



  return (
    <Box
      sx={{
        display: "flex",
        flexDirection: "column",
        minHeight: "100vh",
        backgroundColor: "#fafafa",
      }}
    >
      <MyHeader />
      <Container maxWidth="lg" sx={{ flexGrow: 1, padding: "16px" }}>
        <Grid container spacing={4}>
          {/* 뉴스 섹션 */}
          <Grid item xs={12} md={6}>
            <Box
              sx={{
                padding: 3,
                borderRadius: 2,
                boxShadow: 2,
                backgroundColor: "#fff",
              }}
            >
              <Typography variant="h5" gutterBottom>
                뉴스
              </Typography>
              {newsData.map((news) => (
                <Link href={news.hrefUrl} underline="hover" key={news.id}>
                  <Card
                    sx={{
                      marginBottom: 2,
                      transition: "0.3s",
                      "&:hover": { transform: "scale(1.03)", boxShadow: 3 },
                    }}
                  >
                    <CardMedia
                      component="img"
                      height="280" // 높이를 조금 더 키워줌
                      image={news.imageUrl}
                      alt={news.title}
                    />
                    <CardContent>
                      <Typography variant="h6">{news.title}</Typography>
                      <Typography variant="body2" color="text.secondary">
                        {news.content}
                      </Typography>
                      <Typography
                        variant="body2"
                        color="text.secondary"
                        sx={{ marginTop: 1 }}
                      >
                        {news.createdAt}
                      </Typography>
                    </CardContent>
                  </Card>
                </Link>
              ))}
            </Box>
          </Grid>

          {/* 축구 경기 섹션 */}
          <Grid item xs={12} md={6}>
            <Box
              sx={{
                padding: 3,
                borderRadius: 2,
                boxShadow: 2,
                backgroundColor: "#fff",
              }}
            >
              <Typography variant="h5" gutterBottom>
                이번 주 프리미어 리그 경기
              </Typography>
              {gameFixtureData.map((game) => (
                <Card
                  key={game.date}
                  sx={{
                    marginBottom: 2,
                    borderRadius: 2,
                    transition: "0.3s",
                    "&:hover": { transform: "scale(1.03)", boxShadow: 3 },
                  }}
                >
                  <CardContent>
                    <Box
                      display="flex"
                      alignItems="center"
                      justifyContent="space-between"
                      flexDirection={isMobile ? "column" : "row"}
                    >
                      <Box
                        display="flex"
                        alignItems="center"
                        mb={isMobile ? 1 : 0}
                      >
                        <img
                          src={game.team1.logo}
                          alt={game.team1.name}
                          style={{ width: "30px", marginRight: "5px" }}
                        />
                        <Typography variant="h6" sx={{ marginRight: "10px" }}>
                          {game.team1.name}
                        </Typography>
                      </Box>
                      <Typography variant="h6" textAlign="center">
                        vs
                      </Typography>
                      <Box
                        display="flex"
                        alignItems="center"
                        mt={isMobile ? 1 : 0}
                      >
                        <Typography variant="h6" sx={{ marginRight: "10px" }}>
                          {game.team2.name}
                        </Typography>
                        <img
                          src={game.team2.logo}
                          alt={game.team2.name}
                          style={{ width: "30px", marginLeft: "5px" }}
                        />
                      </Box>
                    </Box>
                    <Typography variant="body2" color="text.secondary">
                      날짜: {game.date}
                    </Typography>
                  </CardContent>
                </Card>
              ))}
            </Box>
          </Grid>
        </Grid>
      </Container>
      <MyFooter />
    </Box>
  );
}
