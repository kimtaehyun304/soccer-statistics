package com.daelim.sfa.dto;

import com.daelim.sfa.domain.News;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
public class NewsDto {

    private String title;

    private String content;

    private LocalDateTime createdAt;

    private String imageUrl;

    private String hrefUrl;

    public NewsDto(News news) {
        this.title = news.getTitle();
        this.content = news.getContent();
        this.createdAt = news.getCreatedAt();
        this.imageUrl = news.getImageUrl();
        this.hrefUrl = news.getHrefUrl();
    }
}
