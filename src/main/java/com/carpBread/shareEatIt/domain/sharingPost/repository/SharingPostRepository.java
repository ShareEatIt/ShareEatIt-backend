package com.carpBread.shareEatIt.domain.sharingPost.repository;

import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.sharingPost.entity.PostStatus;
import com.carpBread.shareEatIt.domain.sharingPost.entity.SharingPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;

public interface SharingPostRepository extends JpaRepository<SharingPost, Long> {

    @Query(value = "select * from sharing_posts where ST_Distance_Sphere(location_point, ST_GeomFromText(CONCAT('POINT(', :latitude, ' ', :longitude, ')'), 4326)) <= :radius and status in ('AVAILABLE','CHATTING')", nativeQuery = true)
    List<SharingPost> findSharingPostsWithinRadius(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("radius") double radius
    );

    @Query(value = "select * from sharing_posts where post_type = :postType and ST_Distance_Sphere(location_point, ST_GeomFromText(CONCAT('POINT(', :latitude, ' ', :longitude, ')'), 4326)) <= :radius AND status in ('AVAILABLE','CHATTING')", nativeQuery = true)
    List<SharingPost> findSharingPostsByPostTypeWithinRadius(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("radius") double radius,
            @Param("postType") String postType
    );

    @Query(value = "SELECT COUNT(p) FROM SharingPost p WHERE p.writer = :writer")
    Long countByWriter(@Param("writer") Member writer);


    List<SharingPost> findByWriter(Member writer);

    List<SharingPost> findAllByNoticedFalseAndStatus(PostStatus status);

    @Query(value = "SELECT p.category, count(p) from SharingPost p where p.writer = :writer group by p.category")
    List<Object[]> countByCategoryForWriter (@Param("writer")Member writer);

}
