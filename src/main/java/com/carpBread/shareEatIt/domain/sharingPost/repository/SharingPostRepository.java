package com.carpBread.shareEatIt.domain.sharingPost.repository;

import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.sharingPost.entity.PostStatus;
import com.carpBread.shareEatIt.domain.sharingPost.entity.SharingPost;
import com.querydsl.core.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;

public interface SharingPostRepository extends JpaRepository<SharingPost, Long> {

    @Query(value = "SELECT COUNT(p) FROM SharingPost p WHERE p.writer = :writer")
    Long countByWriter(@Param("writer") Member writer);

    @Query(value = "SELECT p.category, count(p) from SharingPost p where p.writer = :writer group by p.category")
    List<Tuple> countByCategoryForWriter (@Param("writer")Member writer);

}
