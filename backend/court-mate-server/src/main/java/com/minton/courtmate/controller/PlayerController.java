package com.minton.courtmate.controller;

import com.minton.courtmate.dto.PlayerCreateReq;
import com.minton.courtmate.dto.PlayerCreateRes;
import com.minton.courtmate.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/players")
@RequiredArgsConstructor
public class PlayerController {

  private final PlayerService playerService;

  /**
   * 선수 추가
   */
  @PostMapping
  public ResponseEntity addPlayer(@RequestBody PlayerCreateReq req){
    return ResponseEntity.ok(playerService.addPlayer(req));
  }

  /**
   * 선수 전체 조회
   */
  @GetMapping
  public ResponseEntity<List<PlayerCreateRes>> getAllPlayers() {
    return ResponseEntity.ok(playerService.findAll());
  }

  /**
   * 특정 선수 조회 (id)
   */
  @GetMapping("/{id}")
  public ResponseEntity<PlayerCreateRes> getPlayerById(@PathVariable int id) {
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