package com.carpBread.shareEatIt.domain.member.repository;

import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.member.entity.QMember;
import com.querydsl.core.types.dsl.ComparablePath;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Repository;

import java.util.List;

/* MemberQuerydslRepository의 구현체 Impl */
@Repository
@RequiredArgsConstructor
public class MemberQuerydslRepositoryImpl implements MemberQuerydslRepository{

    // querydsl로 쿼리를 작성할 수 있게 해주는 queryfactory, QuerydslConfig에서 설정함
    private final JPAQueryFactory query;
    private final GeometryFactory geometryFactory;

    // 주어진 위도, 경도와 사용자의 location_point와의 거리가 radius 이하인 Member 리스트를 조회한다.
    @Override
    public List<Member> findMemberWithRadius(double latitude, double longitude, double radius) {
        QMember member = QMember.member;


        // 주어진 위도 경도를 기반으로 point 객체 생성
        Point currentLocation =geometryFactory.createPoint(
                new Coordinate(longitude,latitude)
        );
        currentLocation.setSRID(4326); // GPS의 기준이 되는 좌표계 SRS가 4326이다

        // 쿼리
        List<Member> memberList = query.selectFrom(member)
                .where(
                        // 여부 확인을 위한 booleanTemplate expression 템플릿 사용
                        Expressions.booleanTemplate(
                                "ST_Contains(ST_Buffer({0}, {1}), {2})",
                                currentLocation,
                                radius,
                                member.locationPoint
                        )

                )
                .fetch();

        return memberList;
    }
}
