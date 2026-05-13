package com.minton.court_manager.dto;

import com.minton.court_manager.domain.Player;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PlayerReqDto {

  private String name;
  private Player.Sex sex;
  private String level;

  public Player toEntity() {
    return Player.builder()
        .name(name)
        .sex(sex)
        .level(level)
        .build();
  }
}