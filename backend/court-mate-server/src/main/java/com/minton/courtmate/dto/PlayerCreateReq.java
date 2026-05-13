package com.minton.courtmate.dto;

import com.minton.courtmate.domain.Player;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 플레이어 생성 Request DTO
 */
@Getter
@NoArgsConstructor
public class PlayerCreateReq {

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