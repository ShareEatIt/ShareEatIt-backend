package com.carpBread.shareEatIt.domain.participation.repository;

import com.carpBread.shareEatIt.domain.participation.entity.Participation;
import com.carpBread.shareEatIt.domain.sharingPost.entity.SharingPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {

    @Query("SELECT p.post FROM Participation p WHERE p.receiver.id = :memberId AND p.status = COMPLETED ORDER BY p.createdAt DESC")
    List<SharingPost> findSharingPostByUserAndStatus(@Param("memberId") Long memberId);

}
