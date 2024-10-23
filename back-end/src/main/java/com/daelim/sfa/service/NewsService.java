package com.daelim.sfa.service;

import com.daelim.sfa.domain.News;
import com.daelim.sfa.repository.NewsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class NewsService {

    private final JdbcTemplate jdbcTemplate;

    private final NewsRepository newsRepository;

    public void save(News news) {
        newsRepository.save(news);
    }

    public void saveNewsList(List<News> newsList) {

        jdbcTemplate.batchUpdate("INSERT INTO news(title, content, created_at, image_url, href_url) " +
                "values (?, ?, ?, ?, ?)", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, newsList.get(i).getTitle());
                ps.setString(2, newsList.get(i).getContent());
                ps.setTimestamp(3, Timestamp.valueOf(newsList.get(i).getCreatedAt()));
                ps.setString(4, newsList.get(i).getImageUrl());
                ps.setString(5, newsList.get(i).getHrefUrl());
            }
            @Override
            public int getBatchSize() {
                return newsList.size();
            }
        });
    }

    public void deleteAll(){
        newsRepository.deleteAll();
    }

}
