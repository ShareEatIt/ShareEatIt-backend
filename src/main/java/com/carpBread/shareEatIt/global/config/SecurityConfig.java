package com.carpBread.shareEatIt.global.config;

import com.carpBread.shareEatIt.domain.auth.handler.CustomAuthenticationSuccessHandler;
import com.carpBread.shareEatIt.domain.auth.oauth2.OAuth2LogoutHandler;
import com.carpBread.shareEatIt.domain.auth.oauth2.handler.OAuth2SuccessHandler;
import com.carpBread.shareEatIt.domain.auth.oauth2.repository.OAuth2TokenRepository;
import com.carpBread.shareEatIt.domain.auth.oauth2.service.CustomOAuth2UserService;
import com.carpBread.shareEatIt.domain.auth.util.JWTFilter;
import com.carpBread.shareEatIt.domain.auth.util.JWTUtils;
import com.carpBread.shareEatIt.domain.member.repository.MemberRepository;
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
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.function.client.WebClient;

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
    private final OAuth2TokenRepository oAuth2TokenRepository;

    // http connection
    private final ObjectMapper objectMapper;
    private final WebClient webClient;

    // redis
    private final RedisTemplate<String , Object> redisTemplate;

    // handler
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final CustomAuthenticationSuccessHandler authenticationSuccessHandler;
    private final CustomOAuth2UserService oAuth2UserService;
    private final OAuth2LogoutHandler oAuth2LogoutHandler;

    // 인증이 필요없는 URL 패턴 목록을 정의
    private static final String[] AUTH_WHITELIST = {
            "/login/**", // 로그인
            "/ws/**",
            "/oauth2/**",
            "/auth/refresh",
            "/sentry",
            "/signup"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // form login 활성화
            .formLogin(login -> login
                    .usernameParameter("username")
                    .passwordParameter("password")
                    .successHandler(authenticationSuccessHandler)
            )
            .httpBasic(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(request-> request
                    .requestMatchers(AUTH_WHITELIST).permitAll()  // 채팅 엔드포인트 인증 제외함
                    .anyRequest().hasRole("MEMBER")
            )
            .oauth2Login(oauth2 ->oauth2
                    .successHandler(oAuth2SuccessHandler)
                    .userInfoEndpoint(endpoint-> endpoint.userService(oAuth2UserService)))
            .addFilterBefore(new JWTFilter(jwtUtils,memberRepository,objectMapper,redisTemplate), UsernamePasswordAuthenticationFilter.class)
            .logout(logout -> logout
                    .addLogoutHandler(oAuth2LogoutHandler)
                    .logoutUrl("/logout")
            );
        ;
        return http.build();

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
        configuration.addAllowedOrigin("http://localhost:5173");
        configuration.addAllowedOrigin("http://localhost:6379");
        configuration.addAllowedOrigin("http://localhost:8080");
        configuration.addAllowedOrigin("https://shareeatit.netlify.app");
        configuration.addAllowedOrigin("https://api.shareeat.r-e.kr");
//        configuration.addAllowedOrigin("http://54.180.228.54:8080");


        configuration.addAllowedMethod("GET");
        configuration.addAllowedMethod("POST");
        configuration.addAllowedMethod("PUT");
        configuration.addAllowedMethod("PATCH");
        configuration.addAllowedMethod("DELETE");
        configuration.addAllowedMethod("OPTIONS");
        configuration.addAllowedHeader("*");
        // 헤더에 authorization항목이 있으므로 credential을 true로 설정합니다.
        configuration.setAllowCredentials(true);
        // 채팅 관련 설정
        configuration.setAllowedOriginPatterns(Arrays.asList(
                "https://jiangxy.github.io"));

        source.registerCorsConfiguration("/**",configuration);

        return source;
    }
}
