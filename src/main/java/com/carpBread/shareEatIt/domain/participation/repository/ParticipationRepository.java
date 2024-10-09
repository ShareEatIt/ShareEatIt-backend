package com.carpBread.shareEatIt.domain.participation.repository;

import com.carpBread.shareEatIt.domain.participation.entity.Participation;
import com.carpBread.shareEatIt.domain.sharingPost.entity.PostType;
import com.carpBread.shareEatIt.domain.sharingPost.entity.SharingPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {

    /* 사용자가 나눔받은 모든 기록 조회 */
    @Query("SELECT p.post FROM Participation p WHERE p.receiver.id = :memberId AND p.status = COMPLETED ORDER BY p.createdAt DESC")
    List<SharingPost> findSharingPostByUserAndStatus(@Param("memberId") Long memberId);

    /* 사용자가 특정 provider의 나눔을 받은 모든 기록 조회 */
    @Query("SELECT p.post FROM Participation p WHERE p.receiver.id = :memberId AND p.status = COMPLETED AND p.post.postType = :provider ORDER BY p.createdAt DESC")
    List<SharingPost> findSharingPostByUserAndStatusAndPostType(@Param("memberId") Long memberId, @Param("provider") PostType provider);

    /* 나눔글Id로 완료된 상태의 참여 조회 */
    @Query("SELECT p FROM Participation p WHERE p.post.id = :postId AND p.status = COMPLETED")
    List<Participation> findByPostIdAndStatus(@Param("postId") Long postId);
}
