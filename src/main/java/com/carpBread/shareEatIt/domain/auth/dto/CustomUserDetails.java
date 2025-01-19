package com.carpBread.shareEatIt.domain.auth.dto;

import com.carpBread.shareEatIt.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Getter
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {

    private Member member;

    // 특정기간 이상 활동하지 않은 회원의 계정이 만료되지 않았는지 여부
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 회원의 계정이 잠겨 있지 않았는지 여부
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 비밀번호 만료되지 않았는지 여부
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 사용자 계정 비활성화/활성화 여부
    @Override
    public boolean isEnabled() {
        return true;
    }

    // 사용자의 권한 - @PreAuthorize, @Secured 관련 설정
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    // 사용자 로그인 password
    @Override
    public String getPassword() {
        return member.getPassword();
    }


    // 사용자 로그인 username
    @Override
    public String getUsername() {
        return member.getUsername();
    }

    // social provider
}
