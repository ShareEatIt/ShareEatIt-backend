package com.carpBread.shareEatIt.global.config;

import com.carpBread.shareEatIt.domain.auth.handler.CustomAuthenticationFailureHandler;
import com.carpBread.shareEatIt.domain.auth.handler.CustomAuthenticationSuccessHandler;
import com.carpBread.shareEatIt.domain.auth.handler.CustomLogoutSuccessHandler;
import com.carpBread.shareEatIt.domain.auth.oauth2.handler.OAuth2FailureHandler;
import com.carpBread.shareEatIt.domain.auth.handler.CustomLogoutHandler;
import com.carpBread.shareEatIt.domain.auth.oauth2.handler.OAuth2SuccessHandler;
import com.carpBread.shareEatIt.domain.auth.oauth2.service.CustomOAuth2UserService;
import com.carpBread.shareEatIt.global.entity.HttpCookieOAuth2AuthorizationRequestRepository;
import com.carpBread.shareEatIt.global.jwt.JWTFilter;
import com.carpBread.shareEatIt.global.jwt.JWTUtils;
import com.carpBread.shareEatIt.domain.member.repository.MemberRepository;
import com.carpBread.shareEatIt.global.jwt.JWTCustomExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // jwt
    private final JWTUtils jwtUtils;

    // repository
    private final MemberRepository memberRepository;

    // http connection
    private final ObjectMapper objectMapper;

    // redis
    private final RedisTemplate<String , Object> redisTemplate;

    // handler
    private final JWTCustomExceptionHandler jwtCustomExceptionHandler;
    private final AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository;

    // oauth2
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2FailureHandler oAuth2FailureHandler;
    private final CustomOAuth2UserService oAuth2UserService;
    private final CustomLogoutHandler customLogoutHandler;
    private final CustomLogoutSuccessHandler customLogoutSuccessHandler;

    // local login
    private final CustomAuthenticationSuccessHandler authenticationSuccessHandler;
    private final CustomAuthenticationFailureHandler authenticationFailureHandler;

    // 인증이 필요없는 URL 패턴 목록을 정의
    private static final String[] AUTH_WHITELIST = {
            "/login/**", // 로그인
            "/ws/**",
            "/oauth2/**",
            "/auth/refresh",
            "/sentry",
            "/actuator/health",
            "/",
            "/signup"
    };

    /* security filter chain 설정 */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
            .requiresChannel(channel ->
                    channel.requestMatchers("/login**").requiresSecure() // HTTP 요청을 HTTPS 로 강제 리디렉션
            )
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .addFilterBefore(new JWTFilter(jwtUtils, memberRepository, redisTemplate), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(jwtCustomExceptionHandler, JWTFilter.class)
            // form login 활성화
            .formLogin(login -> login
                    .usernameParameter("username")
                    .passwordParameter("password")
                    .successHandler(authenticationSuccessHandler)
                    .failureHandler(authenticationFailureHandler)
            )
            .httpBasic(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session
                            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(request-> request
                    .requestMatchers(AUTH_WHITELIST).permitAll()  // 채팅 엔드포인트 인증 제외함
                    .anyRequest().hasRole("MEMBER")
            )
            .oauth2Login(oauth2 ->oauth2
                    .authorizationEndpoint(endpoint -> endpoint
                            .authorizationRequestRepository(authorizationRequestRepository)
                    )
                    .successHandler(oAuth2SuccessHandler)
                    .failureHandler(oAuth2FailureHandler)
                    .userInfoEndpoint(endpoint-> endpoint
                            .userService(oAuth2UserService))
            )
            .logout(logout -> logout
                    .addLogoutHandler(customLogoutHandler)
                    .logoutSuccessHandler(customLogoutSuccessHandler)
                    .logoutUrl("/logout")
                    .invalidateHttpSession(true) // 세션 무효화
                    .deleteCookies("JSESSIONID")
                    .clearAuthentication(true)
            );
        return http.build();

    }

    @Bean
    public AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository(){
        return new HttpCookieOAuth2AuthorizationRequestRepository();
    }

    /* 비밀번호 암호화 해시 함수 bean 등록 */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    /* cors 허용 범위 설정 */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOrigin("http://localhost:3000");
        configuration.addAllowedOrigin("https://localhost:3000");
        configuration.addAllowedOrigin("http://localhost:5173");
        configuration.addAllowedOrigin("http://localhost:6379");
        configuration.addAllowedOrigin("http://localhost:8080");
        configuration.addAllowedOrigin("https://shareeatit.netlify.app");
        configuration.addAllowedOrigin("https://api.shareeat.r-e.kr");
        configuration.addAllowedOrigin("https://shareeatit-api.r-e.kr");
        configuration.addAllowedOrigin("https://shareEatIt-server-ELB-904686182.ap-northeast-2.elb.amazonaws.com");

        configuration.addAllowedMethod("GET");
        configuration.addAllowedMethod("POST");
        configuration.addAllowedMethod("PUT");
        configuration.addAllowedMethod("PATCH");
        configuration.addAllowedMethod("DELETE");
        configuration.addAllowedMethod("OPTIONS");
        configuration.addAllowedHeader("*");
        // 헤더에 authorization항목이 있으므로 credential을 true로 설정합니다.
        configuration.setAllowCredentials(true);

        // custom header 지정
        configuration.addExposedHeader("Authorization");
        configuration.addExposedHeader("RT-token");

        // 채팅 관련 설정
        configuration.setAllowedOriginPatterns(Arrays.asList(
                "https://jiangxy.github.io"));

        source.registerCorsConfiguration("/**",configuration);

        return source;
    }
}
