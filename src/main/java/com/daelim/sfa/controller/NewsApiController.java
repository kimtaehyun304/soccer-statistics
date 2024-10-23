package com.daelim.sfa.controller;

import com.daelim.sfa.domain.News;
import com.daelim.sfa.dto.NewsDto;
import com.daelim.sfa.repository.NewsRepository;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class NewsApiController {

    private final NewsRepository newsRepository;

    @GetMapping("/api/news")
    @Operation(summary = "뉴스 조회", description = "요약된 뉴스를 가져옵니다")
    public List<NewsDto> findAll(){
        List<News> news = newsRepository.findAll();
        return news.stream().map(NewsDto::new).toList();
    }

}
