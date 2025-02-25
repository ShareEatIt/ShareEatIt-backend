package com.carpBread.shareEatIt.domain.sharingPost.repository;

import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.sharingPost.dto.response.stats.SimpleStatsCurrentResponseComponent;
import com.carpBread.shareEatIt.domain.sharingPost.entity.PostType;
import com.carpBread.shareEatIt.domain.sharingPost.entity.SharingPost;
import com.querydsl.core.Tuple;
import org.springframework.cglib.core.Local;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SharingPostQuerydslRepository {

    // radius 내 모든 sharingPost 리스트 조회
    List<SharingPost> findSharingPostsWithinRadius(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("radius") double radius
    );

    // radius 내, PostType 에 따른 sharingPost 리스트 조회
    List<SharingPost> findSharingPostsByPostTypeWithinRadius(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("radius") double radius,
            @Param("postType") PostType postType
    );

    // 기간 내 작성자가 작성한 sharingPost list
    List<SharingPost> findByWriterInPeriod(Member writer,
                                           LocalDateTime startAt,
                                           LocalDateTime endAt);

    // 전체 나눔 사용자 중 순위
    int findSharingRank(Member member);

    // radius 내 나눔 사용자 중 순위
    int findSharingRankInRadius(Member writer,
                                          double radius);

    // 사용자의 해당 연도, 해당 월별 나눔글 작성 수
    SimpleStatsCurrentResponseComponent findCurrentStatsByMonth(Member member, LocalDate now, int month);

    // 사용자의 해당 월, 해당 주차별 나눔글 작성 수
    SimpleStatsCurrentResponseComponent findCurrentStatsByWeek(Member member, LocalDate now, int week);

    // 글 작성자가 작성한 shringpost의 수
    Long countByWriter(@Param("writer") Member writer);

    // member 작성자가 쓴 category 별 sharingPost의 글 개수
    List<Tuple> countByCategoryForWriter (@Param("writer")Member writer);

}
