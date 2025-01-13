package com.carpBread.shareEatIt.config;

import com.carpBread.shareEatIt.domain.auth.OAuth2Principal;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.member.entity.Provider;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {
    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser annotation) {

        SecurityContext context = SecurityContextHolder.createEmptyContext();

        Member member = Member.builder()
                .id(1L)
                .email(annotation.email())
                .nickname(annotation.nickname())
                .provider(Provider.STORE)
                .isNoticeAvail(true)
                .isKeywordAvail(true)
                .build();

        OAuth2Principal principal = new OAuth2Principal(member);
        SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority("ROLE_MEMBER");
        UsernamePasswordAuthenticationToken authenticationToken= new UsernamePasswordAuthenticationToken(principal,"kakao", Collections.singleton(grantedAuthority));
        context.setAuthentication(authenticationToken);

        return context;
    }
}
