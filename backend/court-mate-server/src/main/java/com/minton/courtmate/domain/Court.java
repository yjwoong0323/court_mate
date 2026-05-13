package com.minton.courtmate.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "court")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Court {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(nullable = false, length = 20)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(name = "court_type", nullable = false)
  private CourtType courtType;

  public enum CourtType {
    ACTIVE, WAITING
  }

  @Builder
  public Court(String name, CourtType courtType){
    this.name = name;
    this.courtType = courtType;
  }
}
