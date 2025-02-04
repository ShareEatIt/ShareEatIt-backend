package com.carpBread.shareEatIt.domain.participation.repository;

import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.participation.entity.Participation;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


/* participation querydsl 메소드 인터페이스 */
@Repository
public interface ParticipationQuerydslRepository {

    // 기간 내 참여자가 참여한 participation list
    List<Participation> findByReceiverInPeriod(Member receiver,
                                               LocalDateTime startAt,
                                               LocalDateTime endAt);

    int findParticipationRankInRadius(Member receiver, double radius);

    int findParticipationRank(Member receiver);

}
