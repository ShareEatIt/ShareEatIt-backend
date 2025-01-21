package com.carpBread.shareEatIt.domain.sharingPost.repository;

import com.carpBread.shareEatIt.domain.member.entity.Member;
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

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SharingPostQuerydslRepositoryImpl implements SharingPostQuerydslRepository{
    private final JPAQueryFactory query;
    private final GeometryFactory geometryFactory;

    @Override
    public List<SharingPost> findSharingPostsWithinRadius(double latitude, double longitude, double radius) {
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
                                "ST_Distance_Sphere({0},{1}) <= {2}",
                                sharingPost.locationPoint,
                                currentLocation,
                                radius
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
                                "ST_Distance_Sphere({0},{1}) <= {2}",
                                sharingPost.locationPoint,
                                currentLocation,
                                radius
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
