package com.minton.court_manager.domain;

import jakarta.annotation.Resource;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "player")
@Getter
@Setter
@NoArgsConstructor
public class Player {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(nullable = false)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(name = "sex", nullable = false)
  private Sex sex;

  @Column(nullable = false)
  private String level;

  @Column(name = "is_attended", nullable = false)
  private Boolean isAttended = false;

  @CreationTimestamp
  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;

  public enum Sex {
    M, W
  }

  @Builder
  public Player(String name, Sex sex, String level){
    this.name = name;
    this.sex = sex;
    this.level = level;
  }
}
