package com.carpBread.shareEatIt.domain.sharingPost.repository;

import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.sharingPost.entity.SharingPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;

public interface SharingPostRepository extends JpaRepository<SharingPost, Long> {

    @Query(value = "select p from SharingPost p where ST_DistanceSphere(p.locationPoint, ST_MakePoint(:longitude, :latitude)) <= :radius", nativeQuery = true)
    List<SharingPost> findSharingPostsWithinRadius(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("radius") double radius
    );

    @Query(value = "select p from SharingPost p where p.postType = :postType and ST_DistanceSphere(p.locationPoint, ST_MakePoint(:longitude, :latitude)) <= :radius", nativeQuery = true)
    List<SharingPost> findSharingPostsByPostTypeWithinRadius(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("radius") double radius,
            @Param("postType") String postType
    );

    Long countByWriter(Member writer);

    List<SharingPost> findByWriter(Member writer);

    @Query(value = "SELECT p.category, count(p) from SharingPost p where p.writer = :writer group by p.category", nativeQuery = true)
    List<Object[]> countByCategoryForWriter (@Param("writer")Member writer);

}
