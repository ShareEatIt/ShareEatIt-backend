package com.carpBread.shareEatIt.domain.participation.repository;

import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.participation.entity.GratitudeSticker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GratitudeStickerRepository extends JpaRepository<GratitudeSticker, Long> {
    /* participationId로 GratitudeSticker 존재 여부 확인 */
    @Query("SELECT CASE WHEN COUNT(g) > 0 THEN true ELSE false END FROM GratitudeSticker g WHERE g.participation.id = :participationId")
    Boolean existsByParticipationId(@Param("participationId") Long participationId);

    @Query(value = "SELECT g.gratitude_type, COUNT(*) FROM GRATITUDE_STICKERS g WHERE g.giver_id = ?1 GROUP BY g.gratitude_type",nativeQuery = true)
    List<Object[]> countByGratitudeTypeByGiver(Long giverId);
}
