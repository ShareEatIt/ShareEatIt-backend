//package com.carpBread.shareEatIt.domain.auth.oauth2;
//
//import com.carpBread.shareEatIt.domain.member.entity.Member;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//
//import java.util.Collection;
//import java.util.Map;
//
//@Getter
//@AllArgsConstructor
//public class CustomOAuth2User implements OAuth2User {
//
//    private Member member;
//
//    @Override
//    public Map<String, Object> getAttributes() {
//        return null;
//    }
//
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return null;
//    }
//
//    @Override
//    public String getName() {
//        return member.getNickname();
//    }
//
//
//}
