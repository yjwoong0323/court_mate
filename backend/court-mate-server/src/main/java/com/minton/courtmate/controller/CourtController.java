package com.minton.courtmate.controller;

import com.minton.courtmate.dto.CourtRes;
import com.minton.courtmate.service.CourtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/courts")
@RequiredArgsConstructor
public class CourtController {

  private final CourtService courtService;

  /**
   * 전체 코트 조회
   */
  @GetMapping
  public ResponseEntity<List<CourtRes>> getAllCourts() {
    return ResponseEntity.ok(courtService.findAll());
  }
}
