import { Typography, Button, Grid, FormControl, InputLabel, Select, MenuItem, SelectChangeEvent } from "@mui/material";
import { ReactNode, useState } from "react";
import { useNavigate } from "react-router-dom";

export default function LeagueSelect(
    {category, leagueName, leagueSeason, onLeagueChange, onSeasonChange }
        : {
            category:string, leagueName: string, leagueSeason: number, onLeagueChange: (event: SelectChangeEvent<string>, child: ReactNode) => void,
            onSeasonChange: (event: SelectChangeEvent<number>, child: ReactNode) => void
        }) {
    const navigate = useNavigate();

    return (
        <>
            <Typography
                variant="h4"
                gutterBottom
                textAlign="center"
                marginTop={2}
                sx={{
                    fontWeight: 'bold',
                    textTransform: 'uppercase',
                    letterSpacing: 3,
                    fontSize: { xs: '1.8rem', sm: '2.2rem', md: '2.5rem' },
                    background: 'linear-gradient(90deg, #1e3a8a, #2563eb)', // 파란색 그라디언트
                    backgroundClip: 'text',
                    color: 'transparent',
                    paddingBottom: 1,
                    marginBottom: 2,
                    borderBottom: '4px solid #2563eb', // 파란색 라인
                    fontFamily: "'Press Start 2P', sans-serif", // 스포츠 느낌 나는 폰트
                }}
            >
                {category} 랭킹 - {leagueName} ({leagueSeason})
            </Typography>
        
            <Button variant="contained" onClick={() => navigate('/ranking/players')} sx={{ marginRight: "10px" }}>선수 랭킹</Button>
            <Button variant="contained" onClick={() => navigate('/ranking/teams')}>팀 랭킹</Button>

            <Grid container spacing={2}>
                <Grid item xs={6}>
                    <FormControl fullWidth variant="outlined" margin="normal">
                        <InputLabel id="season-select-label">Season</InputLabel>
                        <Select
                            labelId="season-select-label"
                            value={leagueSeason}
                            onChange={onSeasonChange}
                            label="Season"
                        >
                            <MenuItem value="2024">2024</MenuItem>
                            <MenuItem value="2023">2023</MenuItem>
                        </Select>
                    </FormControl>
                </Grid>
                <Grid item xs={6}>
                    <FormControl fullWidth variant="outlined" margin="normal">
                        <InputLabel id="league-select-label">League</InputLabel>
                        <Select
                            labelId="league-select-label"
                            value={leagueName}
                            onChange={onLeagueChange}
                            label="League"
                        >
                            <MenuItem value="Premier League">Premier League</MenuItem>
                            <MenuItem value="La Liga">La Liga</MenuItem>
                            <MenuItem value="Bundesliga">Bundesliga</MenuItem>
                            <MenuItem value="Serie A">Serie A</MenuItem>
                        </Select>
                    </FormControl>
                </Grid>
            </Grid>
        </>
    )
}