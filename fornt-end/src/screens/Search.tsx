import React, { useState } from "react";
import {
  Container,
  Typography,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TextField,
  Button,
  Avatar,
  Grid,
  FormControl,
  Select,
  InputLabel,
  MenuItem,
  Box,
  SelectChangeEvent,
} from "@mui/material";
import { useNavigate } from "react-router-dom";
import MyHeader from "../my-component/MyHeader";
import MyFooter from "../my-component/MyFooter";

interface SearchResult {
  name: string;
  imgUrl: string;
}

const App: React.FC = () => {
  const [searchTerm, setSearchTerm] = useState<string>("");
  const [searchResults, setSearchResults] = useState<SearchResult[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const [category, setCategory] = React.useState("player");

  const handleChange = (event: SelectChangeEvent) => {
    setCategory(event.target.value as string);
  };

  const navigate = useNavigate();

  const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearchTerm(e.target.value);
  };

  const handleSearch = async () => {
    setLoading(true);
    setError(null);

    try {
      if (category === "player") {
        const response = await fetch(`https://dldm.kr/api/players?playerName=${searchTerm}`);
        if (!response.ok) throw new Error("검색 결과가 없습니다.");
        const data: { name: string; photo: string }[] = await response.json();
        const formattedData = data.map((item) => ({
          name: item.name,
          imgUrl: item.photo,
        }));
        setSearchResults(formattedData);
      } else if (category === "team") {
        const response = await fetch(
          `https://dldm.kr/api/teams?teamName=${searchTerm}`
        );
        if (!response.ok) throw new Error("검색 결과가 없습니다.");
        const data: { name: string; logo: string }[] = await response.json();
        const formattedData = data.map((item) => ({
          name: item.name,
          imgUrl: item.logo,
        }));
        setSearchResults(formattedData);
      }
    } catch (err: any) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleRowClick = (name: string) => {
    if (category === "player") {
      navigate(`/players/${name}`);
    } else if (category === "team") {
      navigate(`/teams/${name}`);
    }
  };

  return (
    <Box sx={{ display: "flex", flexDirection: "column", minHeight: "100vh" }}>
      <MyHeader />
      <Container
        maxWidth="lg"
        sx={{
          flexGrow: 1, // 남은 공간을 차지하게 설정
          padding: "16px",
        }}
      >
         <Box 
            style={{ margin: "0", textAlign: "center" }} 
           >
              <img 
                src="/images/sss_logo2.png" 
                alt="SSS.gg Logo" 
                style={{ maxWidth: "18%", height: "auto" }} 
              />
        </Box>
        <Grid container spacing={2} style={{ marginBottom: "16px" }}>
          <Grid item xs={12} sm={4} md={3}>
            <Box sx={{ minWidth: 120 }}>
              <FormControl fullWidth>
                <InputLabel id="demo-simple-select-label">Category</InputLabel>
                <Select
                  labelId="demo-simple-select-label"
                  id="demo-simple-select"
                  value={category}
                  label="Category"
                  onChange={handleChange}
                >
                  <MenuItem value="player">Player</MenuItem>
                  <MenuItem value="team">Team</MenuItem>
                </Select>
              </FormControl>
            </Box>
          </Grid>

          <Grid item xs={12} sm={6} md={7}>
            <TextField
              label="선수 또는 팀 이름을 입력하세요"
              variant="outlined"
              fullWidth
              value={searchTerm}
              onChange={handleSearchChange}
            />
          </Grid>
          <Grid item xs={12} sm={2} md={2}>
            <Button
              variant="contained"
              color="primary"
              onClick={handleSearch}
              disabled={loading}
              fullWidth
              style={{ height: "56px" }}
            >
              {loading ? "검색 중..." : "검색"}
            </Button>
          </Grid>
        </Grid>

        {error && (
          <Typography color="error" style={{ margin: "16px 0" }}>
            오류: {error}
          </Typography>
        )}

        <TableContainer component={Paper} style={{ marginTop: "16px" }}>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>사진</TableCell>
                <TableCell>이름</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {searchResults.length > 0 ? (
                searchResults.map((searchResult) => (
                  <TableRow
                    key={searchResult.name}
                    onClick={() => handleRowClick(searchResult.name)}
                    style={{ cursor: "pointer" }}
                  >
                    <TableCell>
                      <Avatar
                        src={searchResult.imgUrl}
                        alt={searchResult.name}
                      />
                    </TableCell>
                    <TableCell>
                      <Typography variant="body2">
                        {searchResult.name}
                      </Typography>
                    </TableCell>
                  </TableRow>
                ))
              ) : (
                <TableRow>
                  <TableCell colSpan={2} align="center">
                    <Typography variant="body2">데이터가 없습니다.</Typography>
                  </TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </TableContainer>
      </Container>
      <MyFooter />
    </Box>
  );
};

export default App;
