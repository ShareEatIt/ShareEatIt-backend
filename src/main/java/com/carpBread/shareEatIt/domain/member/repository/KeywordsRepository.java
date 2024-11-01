package com.carpBread.shareEatIt.domain.member.repository;

import com.carpBread.shareEatIt.domain.member.entity.Keywords;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface KeywordsRepository extends JpaRepository<Keywords, Long> {
    boolean existsByKeywordAndMember(String keyword, Member member);
    boolean existsByIdAndMember(Long id, Member member);

    Optional<Keywords> findByKeywordAndMember(String keyword, Member member);

    Optional<Keywords> findByMemberAndId(Member member, Long id);


    @Query(value = "SELECT k.avail from Keywords k where k.keyword = :keyword and k.member = :member", nativeQuery = true)
    Boolean findAvailByKeywordAndMember(@Param("keyword") String  keyword, @Param("member")Member member);

    List<Keywords> findAllByMember(Member member);
    List<Keywords> findAllByMemberAndAvail(Member member, Boolean avail);
}
