package com.daelim.sfa.domain.player;

import com.daelim.sfa.domain.team.Team;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Player {

    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
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

    public Player(Long id) {
        this.id = id;
    }

    public void addName(String firstName, String lastName){
        this.firstName = firstName;
        this.lastName = lastName;
    }

        /*
    //private Boolean injured;

    // NULL 허용
    @JoinColumn(name = "team_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Team team;

    // players Squads 에서 조회
    // NULL 허용
    //private Integer number;

    // players Squads 에서 조회
    // NULL 허용
    //private String position;


    public void addTeam(Team team){
        this.team = team;
    }

     */

}
