package com.daelim.sfa.domain.player;

import com.daelim.sfa.domain.team.Team;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Player {

    @Id
    //API 스펙에서 제공하는 숫자로 PK 사용
    @Column(name = "player_id")
    private Long id;

    private String firstName;

    private String lastName;

    private int age;

    @Embedded
    private Birth birth;

    private String nationality;

    private String height;

    private String weight;

    private String photo;

    // NULL 허용
    @JoinColumn(name = "team_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Team team;

    // players Squads 에서 조회
    // NULL 허용
    @Enumerated(EnumType.STRING)
    private Position position;

    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "player")
    private List<PlayerStatistics> statisticsList;

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }


    public Player(Long id) {
        this.id = id;
    }

    public void addName(String firstName, String lastName){
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public void updateTeamAndPosition(Team team, Position position){
        this.team = team;
        this.position = position;
        if(team != null) team.getPlayers().add(this);
    }

    //private Boolean injured;

    // players Squads 에서 조회
    // NULL 허용
    //private Integer number;

}
