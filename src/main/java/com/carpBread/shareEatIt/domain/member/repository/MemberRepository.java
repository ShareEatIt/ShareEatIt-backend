package com.carpBread.shareEatIt.domain.member.repository;

import com.carpBread.shareEatIt.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member , Long> {

    Optional<Member> findByEmail(String email);

    @Query(value = "select * from member where ST_Distance_Sphere(location_point, ST_GeomFromText(CONCAT('POINT(', :latitude, ' ', :longitude, ')'), 4326)) <= :radius", nativeQuery = true)
    List<Member> findMemberWithRadius(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("radius") double radius
    );


}
