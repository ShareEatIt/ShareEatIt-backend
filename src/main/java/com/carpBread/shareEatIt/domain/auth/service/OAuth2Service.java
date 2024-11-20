package com.carpBread.shareEatIt.domain.auth.service;

import com.carpBread.shareEatIt.domain.auth.AuthLoginResponseDto;
import com.carpBread.shareEatIt.domain.auth.dto.OAuth2UserInfo;
import com.carpBread.shareEatIt.domain.auth.dto.OAuthLoginDto;
import com.carpBread.shareEatIt.domain.auth.util.JWTUtils;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.member.entity.Provider;
import com.carpBread.shareEatIt.domain.member.repository.MemberRepository;
import com.carpBread.shareEatIt.domain.notice.controller.NoticeController;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.hc.core5.net.URIBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OAuth2Service {

    private final MemberRepository memberRepository;
    private final JWTUtils jwtUtils;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String clientSecret;


    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String redirectUri;
    @Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
    private String tokenUri;
    @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}")
    private String userInfoUri;

    @Value("${spring.security.oauth2.client.registration.kakao.authorization-grant-type}")
    private String authorizedGrantType;


    public String getAccessOAuth2Token(String code) throws IOException, URISyntaxException {

        // url build
        String reqURL = "https://kauth.kakao.com/oauth/token?"
                +"grant_type=authorization_code"
                +"&client_id="+clientId
                +"&redirect_uri="+redirectUri
                +"&code="+code
                +"&client_secret="+clientSecret;
        URL url=new URL(reqURL);

//        URIBuilder uriBuilder = new URIBuilder(tokenUri);
//        uriBuilder.addParameter("grant_type",authorizedGrantType);
//        uriBuilder.addParameter("client_id",clientId);
//        uriBuilder.addParameter("redirect_uri",redirectUri);
//        uriBuilder.addParameter("code",code);
//        uriBuilder.addParameter("client_secret",clientSecret);
//
//
//        URL url = uriBuilder.build().toURL();

        System.out.println("로그 확인용2 : getAccessOAuth2Token");

        try{
            // connection open
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded;charset=utf-8");

            // response
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line="";
            String result = "";
            while((line=br.readLine())!=null){
                result+=line;
            }
            System.out.println("로그 확인용33"+"getAccessOAuth2Token");

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> response = objectMapper.readValue(result, new TypeReference<Map<String, Object>>() {
            });

            return (String) response.get("access_token");

        }catch (Exception e){
            System.out.println(e.getMessage());
        }


        return null;


    }

    public AuthLoginResponseDto getMemberInfo(String oauth2AccessToken) throws IOException{
        URL url = new URL(userInfoUri);

        // Connection
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization","Bearer "+oauth2AccessToken);
        conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // 응답 받기
        int responseCode = conn.getResponseCode();
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        String line ="";
        String result="";

        System.out.println("로그 확인용"+"getMemberInfo");

        while((line=br.readLine())!=null){
            result+=line;
        }

        // 응답에서 사용자 정보 추출
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> response = objectMapper.readValue(result, new TypeReference<Map<String, Object>>() {
        });

        OAuth2UserInfo oAuth2UserInfo= OAuth2UserInfo.of("kakao",response);

        return loginOrJoin(oAuth2UserInfo, oauth2AccessToken);
    }

    @Transactional
    private AuthLoginResponseDto loginOrJoin(OAuth2UserInfo oauth2UserInfo, String oauth2AccessToken){

        Optional<Member> member = memberRepository.findByEmail(oauth2UserInfo.email());
        if (member.isPresent()){
            Member joinedMember = member.get();
            joinedMember.changeAccessToken(oauth2AccessToken);
            Member updatedMember = memberRepository.save(joinedMember);

            String accessToken = "Bearer "+jwtUtils.createToken(updatedMember.getEmail(), updatedMember.getNickname());

            return AuthLoginResponseDto.builder()
                    .isNewMember(false)
                    .accessToken(accessToken)
                    .refreshToken(updatedMember.getRefreshToken())
                    .build();
        }else{
            String refreshToken = jwtUtils.createToken(oauth2UserInfo.email(), oauth2UserInfo.nickname());
            Member newMember = oauth2UserInfo.toEntity(oauth2AccessToken,refreshToken);
            newMember = memberRepository.save(newMember);

            String accessToken = "Bearer "+ jwtUtils.createToken(newMember.getEmail(), newMember.getNickname());

            System.out.println(accessToken);

            return AuthLoginResponseDto.builder()
                    .isNewMember(true)
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        }


    }

}
