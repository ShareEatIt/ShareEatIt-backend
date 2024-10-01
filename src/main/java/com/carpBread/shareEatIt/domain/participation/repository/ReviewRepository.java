package com.carpBread.shareEatIt.domain.participation.repository;

import com.carpBread.shareEatIt.domain.participation.entity.GratitudeSticker;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<GratitudeSticker, Long> {
}
