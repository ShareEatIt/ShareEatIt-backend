package com.carpBread.shareEatIt.domain.auth.oauth2.handler;

import com.carpBread.shareEatIt.domain.auth.LoginProvider;
import com.carpBread.shareEatIt.domain.auth.oauth2.entity.OAuth2Token;
import com.carpBread.shareEatIt.domain.auth.oauth2.repository.OAuth2TokenRepository;
import com.carpBread.shareEatIt.global.jwt.JWTUtils;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.member.repository.MemberRepository;
import com.carpBread.shareEatIt.global.exception.CustomException;
import com.carpBread.shareEatIt.global.exception.CustomExceptionStatus;
import com.carpBread.shareEatIt.global.exception.Domain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/* 사용자 로그아웃 핸들러 */
// @Value를 받기 때문에 이 클래스를 사용하는 다른 클래스에서 반드시 autowired로 입력받아야 하고, new 로 새로운 객체를 생성하면 안된다.
@Component
public class OAuth2LogoutHandler implements LogoutHandler {

    /* KAKAO 관련 변수 */
    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;

    @Value("${spring.security.oauth2.logout-url.kakao}")
    private String kakaoLogoutUrl;

    /* NAVER 관련 변수 */
    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String naverClientId;

    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String naverClientSecret;

    @Value("${spring.security.oauth2.logout-url.naver}")
    private String naverLogoutUrl;

    private final String serviceProvider = "NAVER";


    /* GOOGLE 관련 변수 */
    @Value("${spring.security.oauth2.logout-url.google}")
    private String googleLogoutUrl;

    /* 공통 변수 */
    @Value("${spring.oauth2.logout.direct-url}")
    private String logoutRedirectUri;


    private WebClient webClient;
    private MemberRepository memberRepository;
    private OAuth2TokenRepository oAuth2TokenRepository;
    private JWTUtils jwtUtils;
    private RedisTemplate<String,Object> redisTemplate;
    public OAuth2LogoutHandler(WebClient webClient, MemberRepository memberRepository, OAuth2TokenRepository oAuth2TokenRepository, JWTUtils jwtUtils, RedisTemplate<String,Object> redisTemplate) {

        this.webClient=webClient;
        this.memberRepository=memberRepository;
        this.oAuth2TokenRepository=oAuth2TokenRepository;
        this.jwtUtils=jwtUtils;
        this.redisTemplate=redisTemplate;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        // jwt 추출
        String token = getJwt(request);

        // member 추출
        Member member = getMemberFromJwt(token);

        // redis에 만료된 jwt 처리
        storeJwtInRedis(token);

        // 로그인 provider에 따른 다른 로그아웃 처리
        LoginProvider loginProvider = getLoginProviderFromJwt(token);
        switch (loginProvider.name()){
            case "KAKAO":
                kakaoLogout(member);
                break;
            case "NAVER":
                naverLogout(member);
                break;
            case "GOOGLE":
                googleLogout(member);
                break;
        }

    }

    /* KAKAO 로그아웃 */
    private void kakaoLogout(Member member){
        OAuth2Token oAuth2Token = getOAuth2Token(member, LoginProvider.KAKAO);

        // uriBuilder로 인코딩할 경우 uri() 함수와 같이 이중으로 인코딩되어 원하는 uri 생성이 되지 않는다.
        // UriComponentsBuilder 로 미리 build 하거나, 직접 String으로 만들어주는 것이 낫다.
        webClient.post()
                .uri(kakaoLogoutUrl
                        +"?client_id="+kakaoClientId)
                .header("Authorization", "Bearer "+oAuth2Token.getAccessToken())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnError(error -> {
                    // 로그아웃 중 오류가 발생한 경우
                    throw new CustomException(
                            CustomExceptionStatus.LOGOUT_FAIL,
                            "KAKAO 로그아웃 중 오류가 발생했습니다. \n Error Response : "+error.getMessage(),
                            this.getClass().getSimpleName(),
                            null,
                            Domain.AUTH
                    );
                })
                .subscribe();

    }

    /* NAVER 로그아웃 */
    private void naverLogout(Member member){
        OAuth2Token oAuth2Token = getOAuth2Token(member, LoginProvider.NAVER);

        webClient.post()
                .uri(naverLogoutUrl + "?grant_type=delete&client_id=" + naverClientId
                        + "&client_secret=" + naverClientSecret
                        + "&access_token=" + oAuth2Token.getAccessToken()
                        + "&service_provider=" + serviceProvider)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnError(error -> {
                    // 로그아웃 중 오류가 발생한 경우
                    throw new CustomException(
                            CustomExceptionStatus.LOGOUT_FAIL,
                            "NAVER 로그아웃 중 오류가 발생했습니다. \n Error Response : "+error.getMessage(),
                            this.getClass().getSimpleName(),
                            null,
                            Domain.AUTH
                    );
                })
                .subscribe();

    }

    /* GOOGLE 로그아웃 */
    private void googleLogout(Member member){
        OAuth2Token oAuth2Token = getOAuth2Token(member, LoginProvider.GOOGLE);

        webClient.post()
                .uri(googleLogoutUrl
                        +"?token="+oAuth2Token.getAccessToken()
                )
                .retrieve()
                .bodyToMono(Void.class)
                .doOnError(error -> {
                    // 로그아웃 중 오류가 발생한 경우
                    throw new CustomException(
                            CustomExceptionStatus.LOGOUT_FAIL,
                            "GOOGLE 로그아웃 중 오류가 발생했습니다. \n Error Response : "+error.getMessage(),
                            this.getClass().getSimpleName(),
                            null,
                            Domain.AUTH
                    );
                })
                .subscribe();

    }

    /* JWT 추출 */
    private String getJwt(HttpServletRequest request){
        String token = null;
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);  // Extracts token after "Bearer "
        }else{
            throw new CustomException(
                    CustomExceptionStatus.UNAUTHORIZED_JWT,
                    "유효하지 않은 ACCESS TOKEN입니다.",
                    this.getClass().getSimpleName(),
                    null,
                    Domain.AUTH
            );
        }
        return token;
    }


    /* redis에 JWT 저장하여 만료시킴 */
    private void storeJwtInRedis(String token){
        // JWT의 jti 추출
        String jti = jwtUtils.getJti(token);
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(jti,token);

    }

    /* JWT에서 member 객체 추출 */
    private Member getMemberFromJwt(String token){
        String provider = jwtUtils.getProvider(token);
        String sub = jwtUtils.getSub(token);
        Member member;

        // LOCAL 로그인일 경우 - SUB 가 USERNAME
        if(provider.equals(LoginProvider.LOCAL.name())){
            member = memberRepository.findByUsername(sub)
                    .orElseThrow(() -> new CustomException(
                            CustomExceptionStatus.NOT_FOUND_MEMBER,
                            "해당 USERNAME을 가진 회원 정보를 찾을 수 없습니다",
                            this.getClass().getSimpleName(),
                            sub,
                            Domain.AUTH
                            )
                    );
        }
        // 소셜 로그인일 경우 - SUB 가 EMAIL
        else {
            member = memberRepository.findByEmail(sub)
                    .orElseThrow(() -> new CustomException(
                                    CustomExceptionStatus.NOT_FOUND_MEMBER,
                                    "해당 EMAIL을 가진 회원 정보를 찾을 수 없습니다",
                                    this.getClass().getSimpleName(),
                                    sub,
                                    Domain.AUTH
                            )
                    );
        }
        return member;

    }

    /* JWT에서 LoginProvider 추출 */
    private LoginProvider getLoginProviderFromJwt(String token){
        String provider = jwtUtils.getProvider(token);
        return LoginProvider.toEnum(provider);
    }

    /* Provider 와 Member에 따른 AccessToken 객체 추출 */
    private OAuth2Token getOAuth2Token(Member member, LoginProvider provider){

        return oAuth2TokenRepository.findByMemberAndProvider(member, provider)
                .orElseThrow(()-> new CustomException(
                        CustomExceptionStatus.NOT_FOUND_OAUTH2_ACCESS_TOKEN,
                        "OAUTH2 LOGIN ACCESS TOKEN 정보가 저장되어 있지 않아 OAUTH2 로그아웃을 진행할 수 없습니다.",
                        this.getClass().getSimpleName(),
                        null,
                        Domain.AUTH
                        )
                );
    }
}
