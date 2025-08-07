package com.daelim.sfa.repository;

import com.daelim.sfa.domain.player.PlayerTransfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlayerTransferRepository extends JpaRepository<PlayerTransfer, Long> {

    @Query("select p from PlayerTransfer p join fetch p.inTeam join fetch p.outTeam where p.player.id = :playerId order by p.date desc  ")
    List<PlayerTransfer> findAllByPlayerId(@Param("playerId") Long playerId);

}
