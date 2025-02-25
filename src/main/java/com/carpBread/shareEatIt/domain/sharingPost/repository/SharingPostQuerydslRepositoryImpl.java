package com.carpBread.shareEatIt.domain.sharingPost.repository;

import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.member.entity.QMember;
import com.carpBread.shareEatIt.domain.sharingPost.dto.response.stats.SimpleStatsCurrentResponseComponent;
import com.carpBread.shareEatIt.domain.sharingPost.entity.PostType;
import com.carpBread.shareEatIt.domain.sharingPost.entity.QSharingPost;
import com.carpBread.shareEatIt.domain.sharingPost.entity.SharingPost;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class SharingPostQuerydslRepositoryImpl implements SharingPostQuerydslRepository{

    private final JPAQueryFactory query;
    private final GeometryFactory geometryFactory;
    private QSharingPost sharingPost = QSharingPost.sharingPost;

    // 기간 내 작성자의 나눔글 목록 조회
    @Override
    public List<SharingPost> findByWriterInPeriod(Member writer,
                                                  LocalDateTime startAt,
                                                  LocalDateTime endAt) {


        return query.selectFrom(sharingPost)
                .where(sharingPost.writer.eq(writer))
                .where(sharingPost.createdAt.between(startAt, endAt))
                .fetch();

    }

    // 전체 나눔 사용자 중 순위
    @Override
    public int findSharingRank(Member writer) {
        QMember member = QMember.member;

        List<Tuple> totals= query
                .select(member.id, sharingPost.count())
                .from(sharingPost)
                .join(member).on(sharingPost.writer.eq(member))
                .groupBy(member.id)
                .orderBy(sharingPost.count().desc())
                .fetch();

        int rank=1;
        for (Tuple tuple : totals){
            if (tuple.get(member.id).equals(writer.getId())){
                break;
            }
            rank++;
        }

        return rank;
    }

    // 기간 내 작성자 작성한 sharingPost total
    // 코드 최적화를 위해 SPATIAL INDEX 적용 및 ST_CONTAINS() 함수 사용
    @Override
    public int findSharingRankInRadius(Member writer, double radius) {

        QMember member = QMember.member;

        // 1. 반경 10km 내 Member ID 리스트 가져오기
        List<Long> nearMemberIdList = query.select(member.id)
                .from(member)
                .where(
                        Expressions.booleanTemplate(
                                "ST_Contains(ST_Buffer({0}, {1}), {2})",
                                writer.getLocationPoint(), radius, member.locationPoint
                        )
                )
                .fetch();

        if (nearMemberIdList.isEmpty()) return -1;

        // 2. 각 멤버들의 sharing post 개수 집계 후 순위
        List<Tuple> totals= query
                .select(member.id, sharingPost.count())
                .from(sharingPost)
                .join(member).on(sharingPost.writer.eq(member))
                .where(member.id.in(nearMemberIdList))
                .groupBy(member.id)
                .orderBy(sharingPost.count().desc())
                .fetch();

        // 3. writer의 순위 계산
        int rank =1;
        for(Tuple total : totals){
            Long memberId = total.get(member.id);
            if (memberId.equals(writer.getId())){
                break;
            }
            rank++;
        }

        return rank;
    }

    // 사용자의 해당 연도, 해당 월별 나눔글 작성 수
    @Override
    public SimpleStatsCurrentResponseComponent findCurrentStatsByMonth(Member member, LocalDate now, int month) {

        int year = now.getYear();

        Long count = query.select(sharingPost.count())
                .from(sharingPost)
                .where(sharingPost.createdAt.year().eq(year))
                .where(sharingPost.writer.eq(member))
                .where(sharingPost.createdAt.month().eq(month))
                .fetchOne();
        count = (count!=null)?count:0;

        return SimpleStatsCurrentResponseComponent.builder()
                .unit(month)
                .count(count)
                .build();
    }

    // 사용자의 해당 월, 해당 주차별 나눔글 작성 수
    @Override
    public SimpleStatsCurrentResponseComponent findCurrentStatsByWeek(Member member, LocalDate now, int week) {
        Long count = query.select(sharingPost.count())
                .from(sharingPost)
                .where(
                        sharingPost.createdAt.year().eq(now.getYear()),
                        sharingPost.createdAt.month().eq(now.getMonthValue()),
                        Expressions.numberTemplate(Integer.class, "WEEK({0})", now).eq(week)
                )
                .where(sharingPost.writer.eq(member))
                .fetchOne();

        return SimpleStatsCurrentResponseComponent.builder()
                .unit(week)
                .count(count)
                .build();
    }

    @Override
    public List<SharingPost> findSharingPostsWithinRadius(double latitude, double longitude, double radius) {
        QSharingPost sharingPost = QSharingPost.sharingPost;

        // 주어진 위도 경도를 기반으로 point 객체 생성
        Point currentLocation =geometryFactory.createPoint(
                new Coordinate(longitude,latitude)
        );
        currentLocation.setSRID(4326); // GPS의 기준이 되는 좌표계 SRS가 4326이다

        // 쿼리
        List<SharingPost> sharingPostList = query
                .selectFrom(sharingPost)
                .where(
                        // 여부 확인을 위한 booleanTemplate expression 템플릿 사용
                        Expressions.booleanTemplate(
                                "ST_Contains(ST_Buffer({0}, {1}), {2})",
                                currentLocation,
                                radius,
                                sharingPost.locationPoint
                        )

                )
                .fetch();

        return sharingPostList;
    }

    @Override
    public List<SharingPost> findSharingPostsByPostTypeWithinRadius(double latitude, double longitude, double radius, PostType postType) {
        QSharingPost sharingPost = QSharingPost.sharingPost;

        // 주어진 위도 경도를 기반으로 point 객체 생성
        Point currentLocation =geometryFactory.createPoint(
                new Coordinate(longitude,latitude)
        );
        currentLocation.setSRID(4326); // GPS의 기준이 되는 좌표계 SRS가 4326이다

        // 쿼리
        List<SharingPost> sharingPostList = query.selectFrom(sharingPost)
                .where(
                        // 여부 확인을 위한 booleanTemplate expression 템플릿 사용
                        Expressions.booleanTemplate(
                                "ST_Contains(ST_Buffer({0}, {1}), {2})",
                                currentLocation,
                                radius,
                                sharingPost.locationPoint
                        ),
                        (sharingPost.postType.eq(postType))

                )
                .fetch();

        return sharingPostList;
    }

    @Override
    public Long countByWriter(Member writer) {
        QSharingPost sharingPost = QSharingPost.sharingPost;
        Long count = query.select(sharingPost.count())
                .from(sharingPost)
                .where(sharingPost.writer.eq(writer))
                .fetchOne();

        return count;
    }

    @Override
    public List<Tuple> countByCategoryForWriter(Member writer) {
        QSharingPost sharingPost = QSharingPost.sharingPost;

        List<Tuple> list = query.select(sharingPost.category, sharingPost.count())
                .from(sharingPost)
                .where(sharingPost.writer.eq(writer))
                .groupBy(sharingPost.category)
                .fetch();

        return list;
    }

}
