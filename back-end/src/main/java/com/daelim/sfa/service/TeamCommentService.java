package com.daelim.sfa.service;

import com.daelim.sfa.domain.player.PlayerComment;
import com.daelim.sfa.domain.team.TeamComment;
import com.daelim.sfa.repository.player.PlayerCommentRepository;
import com.daelim.sfa.repository.team.TeamCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamCommentService {

    private final TeamCommentRepository teamCommentRepository;

    public void saveComment(TeamComment teamComment) {
        teamCommentRepository.save(teamComment);
    }

}
