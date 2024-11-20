package com.carpBread.shareEatIt.global.config;

import com.carpBread.shareEatIt.domain.auth.AuthLoginResponseDto;
import com.carpBread.shareEatIt.domain.auth.OAuth2LogoutHandler;
import com.carpBread.shareEatIt.domain.auth.OAuth2Principal;
import com.carpBread.shareEatIt.domain.auth.OAuth2UserService;
import com.carpBread.shareEatIt.domain.auth.util.JWTFilter;
import com.carpBread.shareEatIt.domain.auth.util.JWTUtils;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.member.repository.MemberRepository;
import com.carpBread.shareEatIt.global.exception.AppException;
import com.carpBread.shareEatIt.global.exception.ErrorCode;
import com.carpBread.shareEatIt.global.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JWTUtils jwtUtils;
    private final MemberRepository memberRepository;
    private final ObjectMapper objectMapper;
    private final OAuth2UserService oAuth2UserService;
    private final WebClient webClient;
    private final RedisTemplate<String , Object> redisTemplate;

    // 인증이 필요없는 URL 패턴 목록을 정의
    private static final String[] AUTH_WHITELIST = {
            "/login/**", // 로그인
            "/ws/**",
            "/oauth2/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(request-> request
                    .requestMatchers(AUTH_WHITELIST).permitAll()  // 채팅 엔드포인트 인증 제외함
                    .anyRequest().hasRole("MEMBER")
            )
            .oauth2Login(oauth2->
                    oauth2.userInfoEndpoint(o->o.userService(oAuth2UserService))
                            .successHandler(successHandler())
            )
            .addFilterBefore(new JWTFilter(jwtUtils,memberRepository,objectMapper,redisTemplate), UsernamePasswordAuthenticationFilter.class)
            .logout(logout -> logout
                    .addLogoutHandler(new OAuth2LogoutHandler(webClient,memberRepository,jwtUtils,redisTemplate))
                    .logoutUrl("/logout")
            );
        return http.build();

    }

    @Bean
    public AuthenticationSuccessHandler successHandler(){
        return ((request, response, authentication) -> {
            DefaultOAuth2User defaultOAuth2User = (DefaultOAuth2User) authentication.getPrincipal();

            String email = (String)defaultOAuth2User.getAttributes().get("email");
            String nickname = (String)defaultOAuth2User.getAttributes().get("nickname");

            String newRefreshToken = jwtUtils.createToken(email,nickname);
            Member member = memberRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_MEMBER, "해당 이메일을 통해 회원가입된 멤버를 찾을 수 없습니다", "/login/oauth2/code/kakao"));

            member.updateRefreshToken(newRefreshToken);
            memberRepository.save(member);

            String accessToken = "Bearer "+jwtUtils.createToken(email, nickname);


//            String encodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8.toString());
//            System.out.println(encodedToken);
//
//            // 쿠키 생성
//            Cookie cookie = new Cookie("accessToken", encodedToken);
//            cookie.setPath("/");
//            cookie.setMaxAge(60*60*24);
//            response.addCookie(cookie);

            ApiResponse responseDto = new ApiResponse<AuthLoginResponseDto>(HttpStatus.CREATED.value(), "카카오 소셜 로그인 성공", new AuthLoginResponseDto(accessToken,newRefreshToken, (Boolean) defaultOAuth2User.getAttributes().get("isNewMember")));
            String jsonResponse = objectMapper.writeValueAsString(responseDto);

            response.setStatus(HttpServletResponse.SC_OK);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            response.getWriter().write(jsonResponse);
            response.getWriter().flush();
            response.getWriter().close();


        });
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOrigin("http://localhost:3000");
        configuration.addAllowedOrigin("http://localhost:5173");
        configuration.addAllowedOrigin("http://localhost:6379");
        configuration.addAllowedOrigin("http://localhost:8080");
        configuration.addAllowedOrigin("http://54.180.228.54:8080");


        configuration.addAllowedMethod("GET");
        configuration.addAllowedMethod("POST");
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
