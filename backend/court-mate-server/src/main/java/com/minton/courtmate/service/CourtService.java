package com.minton.courtmate.service;

import com.minton.courtmate.dto.CourtRes;
import com.minton.courtmate.repository.CourtRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourtService {

  private final CourtRepository courtRepository;

  /**
   * 전체 코트 조회
   */
  @Transactional(readOnly = true)
  public List<CourtRes> findAll() {
    return courtRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))
        .stream()
        .map(CourtRes::new)
        .toList();
  }
}
