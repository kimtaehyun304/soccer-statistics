package com.daelim.sfa.domain.player;

import com.daelim.sfa.domain.Member;
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
public class PlayerComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "parent_id")
    private PlayerComment parent;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String content;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "parent")
    @BatchSize(size = 1000)
    private List<PlayerComment> children = new ArrayList<>();

    @Builder
    public PlayerComment(Player player, PlayerComment parent, Member member, String content, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.player = player;
        this.parent = parent;
        this.member = member;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public PlayerComment(Long parentId) {
        this.id = parentId;
    }
}
