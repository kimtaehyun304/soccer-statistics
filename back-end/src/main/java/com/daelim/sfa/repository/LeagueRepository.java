package com.daelim.sfa.repository;

import com.daelim.sfa.domain.League;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeagueRepository extends JpaRepository<League, Long> {

    League findByName(String name);

}
