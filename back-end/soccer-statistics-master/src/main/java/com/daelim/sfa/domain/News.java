package com.daelim.sfa.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "news_id")
    private Long id;

    private String title;

    private String content;

    private LocalDateTime createdAt;

    private String imageUrl;

    private String hrefUrl;

    @Builder
    public News(String title, String content, LocalDateTime createdAt, String imageUrl, String hrefUrl) {
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.imageUrl = imageUrl;
        this.hrefUrl = hrefUrl;
    }
}
