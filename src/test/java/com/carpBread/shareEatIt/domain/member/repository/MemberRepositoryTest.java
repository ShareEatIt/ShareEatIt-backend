package com.carpBread.shareEatIt.domain.member.repository;

import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.member.entity.Provider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/* MemberRepository.java Repository 단위 테스트 */
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MemberRepositoryTest {

    // @DataJpaTest를 통해 MemberRepository 빈 주입
    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("성공 : 이전에 DB에 저장된 이메일을 통해 DB의 Member 객체를 찾는 성공 테스트")
    void findMemberByEmailSuccessTest() {
        // given
        Member member = Member.builder()
                .nickname("test1")
                .email("test@gmail.com")
                .isKeywordAvail(true)
                .isNoticeAvail(true)
                .addressSt("addressSt")
                .addressDetail("addressDetail")
                .provider(Provider.STORE)
                .build();
        memberRepository.save(member);

        // when
        Optional<Member> findMember = memberRepository.findByEmail("test@gmail.com");

        // then
        assertTrue(findMember.isPresent());
        assertThat(findMember.get().getEmail()).isEqualTo(member.getEmail());
        assertThat(findMember.get().getNickname()).isEqualTo(member.getNickname());

    }

    @Test
    @DisplayName("실패 : 이전에 DB에 저장된 이메일이 아닌 다른 이메일을 통해 DB의 Member 객체를 찾지 못하는 실패 테스트")
    void findMemberByEmailFailTest(){
        // given
        Member member = Member.builder()
                .nickname("test1")
                .email("test@gmail.com")
                .isKeywordAvail(true)
                .isNoticeAvail(true)
                .addressSt("addressSt")
                .addressDetail("addressDetail")
                .provider(Provider.STORE)
                .build();
        memberRepository.save(member);

        // when
        Optional<Member> findMember = memberRepository.findByEmail("test1@gmail.com");

        // then
        assertTrue(findMember.isEmpty(),"DB에 저장되지 않은 이메일이므로 해당 Member 객체를 찾을 수 없습니다");

    }



}