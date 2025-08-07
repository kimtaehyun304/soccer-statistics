package com.daelim.sfa.service;

import com.daelim.sfa.domain.player.Player;
import com.daelim.sfa.domain.player.Position;
import com.daelim.sfa.domain.team.Team;
import com.daelim.sfa.repository.player.PlayerRepository;
import com.daelim.sfa.repository.team.TeamRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;

    public Long save(Player player){
        playerRepository.save(player);
        return player.getId();
    }


}
