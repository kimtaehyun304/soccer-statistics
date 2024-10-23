package com.daelim.sfa.controller;

import com.daelim.sfa.domain.News;
import com.daelim.sfa.domain.game.GameFixture;
import com.daelim.sfa.dto.NewsDto;
import com.daelim.sfa.dto.ThisWeekGameFixtureDto;
import com.daelim.sfa.repository.GameFixtureRepository;
import com.daelim.sfa.repository.NewsRepository;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GameFixtureApiController {

    private final GameFixtureRepository gameFixtureRepository;

    @GetMapping("/api/gameFixtures")
    @Operation(summary = "이번 주 프리미어 리그 경기 조회")
    public List<ThisWeekGameFixtureDto> findAllByThisWeek(){
        List<GameFixture> gameFixtures = gameFixtureRepository.findAllByThisWeek();
        return gameFixtures.stream().map(ThisWeekGameFixtureDto::new).toList();
    }


}
