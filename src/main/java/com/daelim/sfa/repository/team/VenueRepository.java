package com.daelim.sfa.repository.team;

import com.daelim.sfa.domain.team.Venue;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class VenueRepository {

    private final EntityManager em;

    public void save(Venue venue) {
        em.persist(venue);
    }

    public Venue findById(Long id) {
        return em.find(Venue.class, id);
    }

    public List<Venue> findAll(){
        return em.createQuery("select v from Venue v", Venue.class).getResultList();
    }

}
