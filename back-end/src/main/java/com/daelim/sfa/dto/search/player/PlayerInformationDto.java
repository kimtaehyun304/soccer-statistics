package com.daelim.sfa.dto.search.player;

import com.daelim.sfa.domain.player.Player;
import jakarta.persistence.Column;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class PlayerInformationDto {

    // firstName + " " + lastName
    private String name;

    private int age;

    private LocalDate birthDate;

    private String birthCountry;

    private String nationality;

    private String height;

    private String weight;

    private String photo;

    public PlayerInformationDto(Player player){
        StringBuilder stringBuilder = new StringBuilder();
        name = stringBuilder.append(player.getFirstName()).append(" ").append(player.getLastName()).toString();
        age = player.getAge();
        birthDate = player.getBirth().getDate();
        birthCountry = player.getBirth().getCountry();
        nationality = player.getNationality();
        height = player.getHeight();
        weight = player.getWeight();
        photo = player.getPhoto();
    }
}
