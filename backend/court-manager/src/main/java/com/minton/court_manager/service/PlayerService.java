package com.minton.court_manager.service;

import com.minton.court_manager.domain.Player;
import com.minton.court_manager.dto.PlayerReqDto;
import com.minton.court_manager.dto.PlayerResDto;
import com.minton.court_manager.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlayerService {

  private final PlayerRepository playerRepository;

  /**
   * 선수 추가
   */
  @Transactional
  public Player create(PlayerReqDto req) {
    return playerRepository.save(req.toEntity());
  }

  /**
   * 선수 전체 조회
   */
  @Transactional(readOnly = true)
  public List<PlayerResDto> findAll() {
    return playerRepository.findAll()
        .stream()
        .map(PlayerResDto::new)
        .toList();
  }

  /**
   * 특정 선수 조회
   */
  @Transactional(readOnly = true)
  public PlayerResDto findById(int id) {
    Player player = playerRepository.findById(id).
        orElseThrow(() -> new IllegalArgumentException("NO PLAYER: " + id));

    return new PlayerResDto(player);
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