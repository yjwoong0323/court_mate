package com.minton.courtmate.repository;

import com.minton.courtmate.domain.Court;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourtRepository extends JpaRepository<Court, Integer>{
}
