package com.carpBread.shareEatIt.config;

import com.carpBread.shareEatIt.domain.auth.dto.AuthenticationPrincipal;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.member.entity.Provider;
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

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                new AuthenticationPrincipal(member),
                annotation.email(),
                Collections.singleton(new SimpleGrantedAuthority("ROLE_MEMBER")));

        context.setAuthentication(authenticationToken);

        return context;
    }
}
