package com.carpBread.shareEatIt.domain.member.repository;

import com.carpBread.shareEatIt.domain.member.entity.Member;
import org.springframework.stereotype.Repository;

import java.util.List;

/* Member 도메인 관련 querydsl 메소드들을 정의한 interface */
@Repository
public interface MemberQuerydslRepository {

    // 주어진 위도, 경도와 사용자의 location_point와의 거리가 radius 이하인 Member 리스트를 조회한다.
    List<Member> findMemberWithRadius(double latitude,
                                      double longitude,
                                      double radius);

}
