package com.minton.court_manager.controller;

import com.minton.court_manager.domain.Player;
import com.minton.court_manager.dto.PlayerReqDto;
import com.minton.court_manager.dto.PlayerResDto;
import com.minton.court_manager.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/players")
@RequiredArgsConstructor
public class PlayerController {

  private final PlayerService playerService;

  /**
   * 선수 추가
   */
  @PostMapping
  public ResponseEntity addPlayer(@RequestBody PlayerReqDto req){
    return ResponseEntity.ok(playerService.create(req));
  }

  /**
   * 선수 전체 조회
   */
  @GetMapping
  public ResponseEntity<List<PlayerResDto>> getAllPlayers() {
    return ResponseEntity.ok(playerService.findAll());
  }

  /**
   * 특정 선수 조회 (id)
   */
  @GetMapping("/{id}")
  public ResponseEntity<PlayerResDto> getPlayerById(@PathVariable int id) {
    return ResponseEntity.ok(playerService.findById(id));
  }

  /**
   * 특정 선수 삭제 (id)
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> removePlayerById(@PathVariable int id) {
    playerService.delete(id);
    return ResponseEntity.noContent().build();
  }
}