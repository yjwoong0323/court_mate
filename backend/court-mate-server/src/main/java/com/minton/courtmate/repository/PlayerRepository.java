package com.minton.courtmate.repository;

import com.minton.courtmate.domain.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player, Integer>{
}
