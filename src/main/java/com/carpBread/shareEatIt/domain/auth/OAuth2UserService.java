package com.carpBread.shareEatIt.domain.auth;

import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.member.entity.Provider;
import com.carpBread.shareEatIt.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    // 이 메소드가 실행됨은, AccessToken이 정상적으로 발급된 상태임을 의미
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // super.loadUser()로 AccessToken으로 user 정보를 조회함
        Map<String, Object> attributes = super.loadUser(userRequest).getAttributes();

        String accessToken = userRequest.getAccessToken().getTokenValue();

        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_MEMBER");

        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        Map<String, Object> findAttributes = extractAttributes(attributes);

        checkJoin(findAttributes,accessToken,(Long) findAttributes.get("id"));

        return new DefaultOAuth2User(authorities,findAttributes, userNameAttributeName);

    }

    private Map<String , Object> extractAttributes(Map<String, Object> attributes){
        Map<String, Object> findAttributes=new HashMap<String , Object>();
        Long id = (Long) attributes.get("id");

        Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
        String email = (String) account.get("email");

        Map<String, String> profile = (Map<String, String>) account.get("profile");
        String nickname = profile.get("nickname");
        String profileImageUrl = profile.get("profile_image_url");

        findAttributes.put("id",id);
        findAttributes.put("email", email);
        findAttributes.put("nickname", nickname);
        findAttributes.put("profile_image_url", profileImageUrl);
        return findAttributes;

    }

    @Transactional
    public void checkJoin(Map<String, Object> attributes, String accessToken, Long accessId){

        String email = (String)attributes.get("email");
        String nickname = (String)attributes.get("nickname");
        String profileImg=(String)attributes.get("profile_image_url");

        Member member = memberRepository.findByEmail(email)
                .orElse(null);

        if(member==null){
            Member newMember= Member.builder()
                    .email(email)
                    .accessToken(accessToken)
                    .accessId(accessId)
                    .nickname(nickname)
                    .profileImgUrl(profileImg)
                    .provider(Provider.INDIVIDUAL)
                    .isKeywordAvail(true)
                    .isNoticeAvail(true)
                    .build();

            Member savedMember = memberRepository.save(newMember);

        }else if(!member.getNickname().equals(nickname)){

            throw new RuntimeException("회원가입되지 않은 사용자입니다.");

        }else{
            member.changeAccessToken(accessToken);
            Member savedMember = memberRepository.save(member);
        }

    }
}
