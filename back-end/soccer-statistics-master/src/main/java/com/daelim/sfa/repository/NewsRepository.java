package com.daelim.sfa.repository;

import com.daelim.sfa.domain.News;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class NewsRepository {

    private final EntityManager em;

    public void save(News news) {
        em.persist(news);
    }

    public void deleteAll() {
        em.createQuery("delete from News").executeUpdate();
    }

    public News findById(Long id) {
        return em.find(News.class, id);
    }


    public List<News> findAll(){
        return em.createQuery("select n from News n", News.class).getResultList();
    }

}
