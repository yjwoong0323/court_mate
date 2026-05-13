package com.minton.courtmate.dto;

import com.minton.courtmate.domain.Player;
import lombok.Getter;

/**
 * 플레이어 생성 Response DTO
 */
@Getter
public class PlayerCreateRes {

  private int id;
  private String name;
  private Player.Sex sex;
  private String level;
  private boolean isAttended;

  public PlayerCreateRes(Player player) {
    this.id = player.getId();
    this.name = player.getName();
    this.sex = player.getSex();
    this.level = player.getLevel();
    this.isAttended = player.getIsAttended();
  }
}