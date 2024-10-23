package com.daelim.sfa.service;

import com.daelim.sfa.domain.player.PlayerComment;
import com.daelim.sfa.repository.player.PlayerCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PlayerCommentService {

    private final PlayerCommentRepository playerCommentRepository;

    public void saveComment(PlayerComment playerComment) {
        playerCommentRepository.save(playerComment);
    }

}
