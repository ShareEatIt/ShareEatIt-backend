package com.carpBread.shareEatIt.domain.member.repository;

import com.carpBread.shareEatIt.config.QuerydslTestConfig;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.member.entity.Provider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/* MemberQuerydslRepositoryImpl.java Repository 단위 테스트 */
@DataJpaTest
 // @DataJpaTest에서 querydsl 관련 요소는 로드가 안되므로 @TestConfiguration으로 관련 빈들을 정의해놓고 import 해 사용한다.
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import(QuerydslTestConfig.class)
class MemberQuerydslRepositoryTest {

    private final Double radius = 10000.0;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberQuerydslRepository memberQuerydslRepository;

    @Test
    @DisplayName("성공 : 저장된 Member의 위치와 10km 이내의 위도/경도를 입력했을 때 사용자를 찾을 수 있는 성공 테스트")
    void findMemberListWithRadiusSuccessTest() {

        // given
        Point memberLocation =new GeometryFactory().createPoint(
                new Coordinate(127.099491,36.798330)
        );
        memberLocation.setSRID(4326);

        Member member = Member.builder()
                .nickname("test1")
                .email("test@gmail.com")
                .isKeywordAvail(true)
                .isNoticeAvail(true)
                .locationPoint(memberLocation)
                .addressSt("충청남도 천안시 서북구 불당동")
                .addressDetail("공원로 176 303동")
                .provider(Provider.STORE)
                .build();
        memberRepository.save(member);

        // when
        List<Member> memberList = memberQuerydslRepository.findMemberWithRadius(36.794754, 127.099738, radius);

        // then
        assertFalse(memberList.isEmpty());
        assertEquals(memberList.get(0).getEmail(), member.getEmail());

    }
}