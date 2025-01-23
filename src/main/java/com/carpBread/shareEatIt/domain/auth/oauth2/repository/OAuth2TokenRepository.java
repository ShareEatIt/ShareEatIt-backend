package com.carpBread.shareEatIt.domain.auth.oauth2.repository;

import com.carpBread.shareEatIt.domain.auth.LoginProvider;
import com.carpBread.shareEatIt.domain.auth.oauth2.entity.OAuth2Token;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/* OAuth2Token의 jpa repository */
public interface OAuth2TokenRepository extends JpaRepository<OAuth2Token, Long> {
    // 해당 member와 provider를 가진 entity 조회
    Optional<OAuth2Token> findByMemberAndProvider(Member member, LoginProvider provider);

}
