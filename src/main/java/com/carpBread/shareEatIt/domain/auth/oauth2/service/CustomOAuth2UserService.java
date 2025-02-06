package com.carpBread.shareEatIt.domain.auth.oauth2.service;

import com.carpBread.shareEatIt.domain.auth.LoginProvider;
import com.carpBread.shareEatIt.domain.auth.oauth2.dto.OAuth2Attribute;
import com.carpBread.shareEatIt.domain.auth.oauth2.entity.OAuth2Token;
import com.carpBread.shareEatIt.domain.auth.oauth2.repository.OAuth2TokenRepository;
import com.carpBread.shareEatIt.global.jwt.JWTUtils;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.member.entity.Provider;
import com.carpBread.shareEatIt.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/* 클라이언트의 인증 및 사용자 정보를 받아온 후 인증 객체 생성 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;
    private final OAuth2TokenRepository oAuth2TokenRepository;
    private final JWTUtils jwtUtils;
    private final GeometryFactory geometryFactory;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        // 0. accessToken을 사용해 사용자 정보 받아와서 OAuth2User 객체 반환
        String accessToken = userRequest.getAccessToken().getTokenValue();
        OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);

        // 1. registrationId provider : 어느 플랫폼 로그인을 진행했는지
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // 2. 사용자 고유 key 추출
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();

        // 3. 사용자 정보 attribute map 추출
        OAuth2Attribute oAuth2Attribute = OAuth2Attribute.of(registrationId,userNameAttributeName, oAuth2User.getAttributes());
        Map<String, Object> attributesMap = oAuth2Attribute.convertToMap();

        // 4. 회원가입되지 않았을 경우 회원가입
        Boolean isJoined = signUpIfNotExists(oAuth2Attribute,accessToken, LoginProvider.toEnum(registrationId));
        attributesMap.put("is_new_member",isJoined);

        // 5. OAuth2User 구현 객체 리턴
        return new DefaultOAuth2User(
                // authorization을 set으로 생성하여 추가가 삭제가 불가하고 단일 객체만 생성 가능
                Collections.singleton(new SimpleGrantedAuthority("ROLE_MEMBER")),
                attributesMap,
                "email"
        );
    }

    /* 회원가입 되어있지 않는 첫 로그인 사용자일 때 회원가입 - 회원가입 여부 반환 */
    private Boolean signUpIfNotExists(OAuth2Attribute attribute, String accessToken, LoginProvider provider){
        // db에서 optional 객체 추출
        Optional<Member> byEmail = memberRepository.findByEmail(attribute.getEmail());

        // refreshToken 생성
        String refreshToken = jwtUtils.createRefreshToken(attribute.getEmail(), provider);

        // 회원가입 되어 있다면 false 리턴
        if (byEmail.isPresent()){
            saveAccessToken(accessToken,byEmail.get(),provider);
            byEmail.get().updateRefreshToken(refreshToken);
            memberRepository.save(byEmail.get());
            return false;
        }

        // point 객체 생성 - 기본 : 서울 광화문 광장 설정
        Double latitude = 37.572447;
        Double longitude= 126.976936;
        Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude));


        // Member 객체 생성
        Member newMember = Member.builder()
                .email(attribute.getEmail())
                .nickname(attribute.getNickname())
                .profileImgUrl(attribute.getProfileImage())
                .provider(Provider.INDIVIDUAL)
                .locationPoint(point)
                .refreshToken(refreshToken)
                .isKeywordAvail(true)
                .isNoticeAvail(true)
                .build();

        // Member 테이블에 저장
        Member saved = memberRepository.save(newMember);

        // new accessToken 저장
        saveAccessToken(accessToken,saved,provider);

        return true;

    }

    /* 로그아웃을 위한 accessToken 저장 */
    private void saveAccessToken(String accessToken, Member member, LoginProvider provider){
        Optional<OAuth2Token> auth2Token = oAuth2TokenRepository.findByMemberAndProvider(member, provider);
        OAuth2Token newToken=null;

        // oauth2 token이 존재하는 경우 - 새로운 객체 저장
        if (auth2Token.isEmpty()){
            newToken = new OAuth2Token(member,provider,accessToken);
        }
        // oauth2 token이 존재하지 않는 경우 - 있는 객체
        else{
            newToken= auth2Token.get();
            newToken.updateAccessToken(accessToken);
        }

        oAuth2TokenRepository.save(newToken);

    }

}
