package com.minton.courtmate.service;

import com.minton.courtmate.domain.Player;
import com.minton.courtmate.dto.PlayerCreateReq;
import com.minton.courtmate.dto.PlayerCreateRes;
import com.minton.courtmate.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlayerService {

  private final PlayerRepository playerRepository;

  /**
   * 선수 추가
   */
  @Transactional
  public Player addPlayer(PlayerCreateReq req) {
    return playerRepository.save(req.toEntity());
  }

  /**
   * 선수 전체 조회
   */
  @Transactional(readOnly = true)
  public List<PlayerCreateRes> findAll() {
    return playerRepository.findAll()
        .stream()
        .map(PlayerCreateRes::new)
        .toList();
  }

  /**
   * 특정 선수 조회
   */
  @Transactional(readOnly = true)
  public PlayerCreateRes findById(int id) {
    Player player = playerRepository.findById(id).
        orElseThrow(() -> new IllegalArgumentException("NO PLAYER: " + id));

    return new PlayerCreateRes(player);
  }

  /**
   * 특정 선수 삭제
   */
  @Transactional
  public void delete(int id) {
    Player player = playerRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("NO PLAYER: " + id));

    playerRepository.deleteById(id);
  }
}