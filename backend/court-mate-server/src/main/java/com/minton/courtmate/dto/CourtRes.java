package com.minton.courtmate.dto;

import com.minton.courtmate.domain.Court;
import lombok.Getter;

@Getter
public class CourtRes {
  private int id;
  private String name;
  private Court.CourtType courtType;

  public CourtRes(Court court) {
    this.id = court.getId();
    this.name = court.getName();
    this.courtType = court.getCourtType();
  }
}
