package com.carpBread.shareEatIt.domain.participation.repository;

import com.carpBread.shareEatIt.domain.participation.entity.Participation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {
}
