package com.carpBread.shareEatIt.domain.participation.repository;

import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.member.entity.QMember;
import com.carpBread.shareEatIt.domain.participation.entity.Participation;
import com.carpBread.shareEatIt.domain.participation.entity.QParticipation;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ParticipationQuerydslRepositoryImpl implements ParticipationQuerydslRepository {
    private final JPAQueryFactory query;
    private QParticipation participation  = QParticipation.participation;

    // 참여자의 기간 내 참여 목록 조회
    @Override
    public List<Participation> findByReceiverInPeriod(Member receiver, LocalDateTime startAt, LocalDateTime endAt) {

        return query.selectFrom(participation)
                .where(participation.receiver.eq(receiver))
                .where(participation.createdAt.between(startAt, endAt))
                .fetch();
    }

    // radius 이내 회원 중 참여 순위
    @Override
    public int findParticipationRankInRadius(Member receiver, double radius) {
        QMember member = QMember.member;

        // 1. 반경 10km 내 Member ID 리스트 가져오기
        List<Long> nearMemberIdList = query.select(member.id)
                .from(member)
                .where(
                        Expressions.booleanTemplate(
                                "ST_Contains(ST_Buffer({0}, {1}),{2})",
                                receiver.getLocationPoint(), radius, member.locationPoint
                        )
                )
                .fetch();

        if (nearMemberIdList.isEmpty()) return -1;

        // 2. 각 멤버들의 참여 횟수 집계 후 순위
        List<Tuple> totals = query
                .select(member.id, participation.count())
                .from(participation)
                .join(member).on(participation.receiver.eq(member))
                .where(member.id.in(nearMemberIdList))
                .groupBy(member.id)
                .orderBy(participation.count().desc())
                .fetch();

        // 3. receiver 순위 계산
        int rank = 1;
        for (Tuple total : totals){
            Long memberId = total.get(member.id);
            if (memberId.equals(receiver.getId())) break;
            rank++;
        }

        return rank;
    }

    // 현재 참여자 중 총 순위
    @Override
    public int findParticipationRank(Member receiver) {
        QMember member=QMember.member;

        List<Tuple> totals= query
                .select(member.id, participation.count())
                .from(participation)
                .join(member).on(participation.receiver.eq(member))
                .groupBy(member.id)
                .orderBy(participation.count().desc())
                .fetch();

        int rank=1;
        for (Tuple tuple : totals){
            if (tuple.get(member.id).equals(receiver.getId())){
                break;
            }
            rank++;
        }

        return rank;
    }
}
