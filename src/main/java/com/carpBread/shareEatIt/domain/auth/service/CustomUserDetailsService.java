package com.carpBread.shareEatIt.domain.auth.service;

import com.carpBread.shareEatIt.domain.auth.dto.CustomUserDetails;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/* 자체 로그인한 유저의 username이 존재하는지 여부를 확인하고 해당 Member를 반환하거나 없으면 에러를 반환하는 서비스 함수 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // username으로 Member 조회
        Member findMember = memberRepository.findByUsername(username)
                .orElseThrow(()-> new UsernameNotFoundException(username+" username을 가진 회원을 찾지 못했습니다."));

        // userdetails 반환
        return new CustomUserDetails(findMember);
    }
}
