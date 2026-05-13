package com.minton.court_manager.dto;

import com.minton.court_manager.domain.Player;
import lombok.Getter;

@Getter
public class PlayerResDto {

  private int id;
  private String name;
  private Player.Sex sex;
  private String level;
  private boolean isAttended;

  public PlayerResDto(Player player) {
    this.id = player.getId();
    this.name = player.getName();
    this.sex = player.getSex();
    this.level = player.getLevel();
    this.isAttended = player.getIsAttended();
  }
}