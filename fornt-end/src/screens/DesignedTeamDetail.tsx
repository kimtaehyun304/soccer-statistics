import React, { useState, useEffect } from "react";
import {
  Box,
  Button,
  CircularProgress,
  Container,
  Typography,
  List,
  ListItem,
  ListItemText,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  FormControl,
  InputLabel,
  MenuItem,
  Select,
  SelectChangeEvent,
} from "@mui/material";
import MyHeader from "../my-component/MyHeader";
import MyFooter from "../my-component/MyFooter";
import MyComment from "../my-component/MyComment";
import { useParams, useSearchParams } from "react-router-dom";

const DesignedTeamDetail: React.FC = () => {
  const { keyword } = useParams<{ keyword: string }>();
  const [teamData, setTeamData] = useState<any>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [showForm, setShowForm] = useState<boolean>(false);

  const [searchParams] = useSearchParams();
  //const leagueName = searchParams.get("leagueName")
  const [leagueSeason, setLeagueSeason] = useState<number>(
    parseInt(searchParams.get("leagueSeason") ?? "2024")
  );

  //const formationImage = require("../assets/formation.png"); // MatchResult.tsx가 src/components에 있을 경우

  useEffect(() => {
    const fetchTeamData = async () => {
      try {
        //const response = await fetch(`http://localhost:8080/api/teams/${keyword}?leagueSeason=${leagueSeason}`);
        const response = await fetch(`https://dldm.kr/api/teams/${keyword}?leagueSeason=${leagueSeason}`);
        const responseData = await response.json();
        setTeamData(responseData);
        setIsLoading(false);
      } catch (error) {
        setError("데이터를 불러오는 중 오류가 발생했습니다.");
        setIsLoading(false);
      }
    };

    fetchTeamData();
  }, [leagueSeason]);

  if (isLoading) {
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
      <Typography color="error" textAlign="center">
        {error}
      </Typography>
    );
  }

  if (!teamData) {
    return (
      <Typography variant="h4" color="error" align="center" mt={10}>
        NO TEAM
      </Typography>
    );
  }

  const handleIconClick = () => {
    setShowForm(true);
  };
  const handleClose = () => {
    setShowForm(false);
  };

  const handleSeasonChange = (event: any) => {
    setLeagueSeason(event.target.value);
  };

  // 득실차 계산
  const goalDifference =
    teamData.statistics.goalTotal - teamData.statistics.againstTotal;
  return (
    <Box sx={{ display: "flex", flexDirection: "column", minHeight: "100vh" }}>
      <MyHeader />

      <Container>
        <Box
          display="flex"
          alignItems="center"
          mb={2}
          padding={2}
          borderBottom={1}
          borderColor="grey.300"
          bgcolor="#f9f9f9"
          borderRadius={1}
          marginTop={1}
        >
          <img
            src={teamData.information.logo}
            alt="팀 로고"
            style={{
              width: "80px",
              height: "80px",
              borderRadius: "50%",
              marginRight: "20px",
            }}
          />
          <Typography variant="h5">{teamData.information.teamName}</Typography>
          <img
            src="/images/formation.png"
            alt="전술 아이콘"
            onClick={handleIconClick}
            style={{
              width: "40px",
              height: "40px",
              cursor: "pointer",
              marginLeft: "auto",
            }}
          />
        </Box>
        {/* 포메이션 폼 */}
        <Dialog open={showForm} onClose={handleClose}>
          <DialogTitle>
            <Box display="flex" alignItems="center">
              <img
                src={teamData.information.logo}
                alt="팀 로고"
                style={{ width: "70px", height: "70px", marginRight: "10px" }}
              />
              <Typography variant="h6" style={{ marginLeft: "5px" }}>
                {teamData.information.teamName} - 포메이션 사용 수
              </Typography>
            </Box>
          </DialogTitle>

          {/* 팀 이름과 순위 사이 구분선 */}
          <DialogContent dividers>
            {/* 포메이션 순위 부분을 감싸는 얇은 선 */}
            <Box
              border="2px solid #ccc"
              borderRadius="20px"
              padding="10px"
              mb={2}
            >
              <List>
                {teamData.lineups
                  .sort((a: any, b: any) => b.played - a.played)
                  .slice(0, 5)
                  .map((lineup: any, index: number) => (
                    <ListItem key={index}>
                      <Box
                        display="flex"
                        alignItems="center"
                        justifyContent="space-between"
                        width="100%"
                      >
                        {/* 순위 */}
                        <Box
                          border="2px solid #2196f3"
                          borderRadius="8px"
                          padding="5px 10px"
                          marginRight="10px"
                          bgcolor="#e3f2fd"
                        >
                          <Typography
                            variant="body1"
                            fontWeight="bold"
                            color="#2196f3"
                          >
                            {index + 1}위
                          </Typography>
                        </Box>

                        {/* 포메이션 정보 */}
                        <ListItemText
                          primary={`포메이션: ${lineup.formation}`}
                          secondary={`사용 수: ${lineup.played}`}
                        />
                      </Box>
                    </ListItem>
                  ))}
              </List>
            </Box>
          </DialogContent>

          <DialogActions>
            <Button onClick={handleClose}>닫기</Button>
          </DialogActions>
        </Dialog>

        {/* 시즌 정보 컨테이너 */}
        <Box
          border={2}
          borderColor="grey.300"
          borderRadius="8px"
          p={2}
          mb={2}
          bgcolor="#f9f9f9"
          display="flex"
          alignItems="center"
          justifyContent="space-between"
          mt={2} // 컨테이너의 위쪽 여백 추가
        >
          {/* 시즌 정보 
          <Typography
            variant="body1"
            fontWeight="bold"
            flex="0 1 150px"
            textAlign="left"
          >
            시즌: {leagueSeason}
          </Typography>
          */}

          <Typography
            variant="body1"
            fontWeight="bold"
            flex="0 1 150px"
            textAlign="left"
          >
            <FormControl>
              <InputLabel id="league-season-label">시즌 선택</InputLabel>
              <Select
                labelId="league-season-label"
                id="league-season"
                value={leagueSeason} // 현재 state와 일치하도록 설정
                onChange={handleSeasonChange}
                label="시즌 선택"
              >
                <MenuItem value={2024}>2024</MenuItem>
                <MenuItem value={2023}>2023</MenuItem>
              </Select>
            </FormControl>
          </Typography>

          {/* 리그 이름 */}
          <Typography
            variant="body1"
            fontWeight="bold"
            flex="1"
            textAlign="center"
          >
            {teamData.leagueName}
          </Typography>

          {/* 레드카드와 옐로우카드 */}
          <Typography
            variant="body1"
            flex="0 1 150px"
            textAlign="right"
            display="flex"
            alignItems="center"
            justifyContent="flex-end"
          >
            {/* 레드 카드 */}
            <span
              style={{
                display: "inline-block",
                backgroundColor: "red",
                width: "18px",
                height: "25px",
                marginRight: "8px",
                borderRadius: "4px", // 좀 더 카드 느낌을 살린 둥근 모서리
              }}
            />
            : {teamData.statistics.redTotal}
            <span style={{ marginRight: "16px" }} />
            {/* 옐로우 카드 */}
            <span
              style={{
                display: "inline-block",
                backgroundColor: "#ffeb3b",
                width: "18px",
                height: "25px",
                marginRight: "8px",
                borderRadius: "4px", // 옐로우 카드도 둥근 모서리
              }}
            />
            : {teamData.statistics.yellowTotal}
          </Typography>
        </Box>

        {/* 경기 통계 컨테이너 (승무패 + 득실차) */}
        <Box
          display="flex"
          justifyContent="space-between"
          alignItems="center"
          mb={2}
          gap={2} // 컨테이너 사이 간격 추가
        >
          {/* 경기 통계 (승, 무, 패) 섹션 */}
          <Box
            flexBasis="48%" // 좌측 컨테이너
            border={2}
            borderColor="grey.300"
            borderRadius="8px"
            p={2}
            bgcolor="#f9f9f9"
            textAlign="center"
          >
            <Typography variant="body1" fontWeight="bold">
              <span style={{ marginRight: "20px" }}>
                {teamData.statistics.played}전
              </span>
              <span style={{ marginRight: "20px" }}>
                {teamData.statistics.wins}승
              </span>
              <span style={{ marginRight: "20px" }}>
                {teamData.statistics.draws}무
              </span>
              <span>{teamData.statistics.losses}패</span>
            </Typography>
          </Box>

          {/* 득실차 섹션 */}
          <Box
            flexBasis="48%" // 우측 컨테이너
            border={2}
            borderColor="grey.300"
            borderRadius="8px"
            p={2}
            bgcolor="#f9f9f9"
            textAlign="center"
          >
            <Typography variant="body1" fontWeight="bold">
              <span style={{ marginRight: "20px" }}>
                득점: {teamData.statistics.goalTotal}
              </span>
              <span style={{ marginRight: "20px" }}>
                실점: {teamData.statistics.againstTotal}
              </span>
              <span>
                득실:{" "}
                {goalDifference >= 0 ? `+${goalDifference}` : goalDifference}
              </span>
            </Typography>
          </Box>
        </Box>

        {/* 경기 결과 컨테이너 */}
        {teamData.gameFixtures.map((match: any, index: number) => {
          let bgColor = "#eef6ff"; // 기본값
          if (match.team1.goals < match.team2.goals) {
            bgColor = "#ffe6e6"; // 팀1이 패배한 경우
          } else if (match.team1.goals === match.team2.goals) {
            bgColor = "#f0f0f0"; // 무승부
          }

          return (
            <Box
              key={index}
              border={2}
              borderColor={
                match.team1.goals > match.team2.goals
                  ? "#007bff"
                  : match.team1.goals < match.team2.goals
                  ? "#ff4d4d"
                  : "#ccc"
              }
              borderRadius="8px"
              mb={2}
              p={2}
              display="flex"
              flexDirection={{ xs: "column", md: "row" }} // 모바일에서 세로 정렬
              justifyContent="space-between"
              alignItems="center"
              bgcolor={bgColor}
            >
              <Box
                display="flex"
                alignItems="center"
                justifyContent="flex-start"
                width={{ xs: "100%", md: "150px" }} // 모바일에서 100% 너비
              >
                <img
                  src={match.team1.logo}
                  alt="팀1 로고"
                  style={{ width: "50px", height: "50px", marginRight: "10px" }}
                />
                <Typography
                  variant="h6"
                  style={{ margin: "0", whiteSpace: "nowrap" }}
                >
                  {match.team1.name}
                </Typography>
              </Box>

              <Box
                display="flex"
                flexDirection="column"
                alignItems="center"
                flex={1}
                textAlign="center" // 중앙 정렬
              >
                <Typography variant="h5" fontWeight="bold" color="#333">
                  {match.team1.goals} - {match.team2.goals}
                </Typography>
                <Typography variant="body2" color="#777">
                  {match.date}
                </Typography>
                <Typography variant="body2" color="#555">
                  {match.homeAway === "HOME" ? "홈" : "원정"} / 심판:{" "}
                  {match.referee}
                </Typography>
              </Box>

              <Box
                display="flex"
                alignItems="center"
                justifyContent="flex-end"
                width={{ xs: "100%", md: "150px" }} // 모바일에서 100% 너비
              >
                <Typography
                  variant="h6"
                  style={{ margin: "0", whiteSpace: "nowrap" }}
                >
                  {match.team2.name}
                </Typography>
                <img
                  src={match.team2.logo}
                  alt="팀2 로고"
                  style={{ width: "50px", height: "50px", marginLeft: "10px" }}
                />
              </Box>
            </Box>
          );
        })}
      </Container>

      {/* 댓글 목록, 페이징, 댓글 등록 */}
      <Container
        style={{ marginTop: "20px", textAlign: "left", marginBottom: "20px" }}
      >
        <MyComment category="teams" />
      </Container>

      <MyFooter />
    </Box>
  );
};

export default DesignedTeamDetail;
