package com.daelim.sfa.domain.team;

import com.daelim.sfa.domain.Member;
import com.daelim.sfa.domain.player.Player;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "parent_id")
    private TeamComment parent;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String content;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "parent")
    @BatchSize(size = 1000)
    private List<TeamComment> children = new ArrayList<>();

    public TeamComment(Long id) {
        this.id = id;
    }

    @Builder
    public TeamComment(Team team, TeamComment parent, Member member, String content, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.team = team;
        this.parent = parent;
        this.member = member;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
