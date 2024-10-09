package com.carpBread.shareEatIt.global.config;

import com.carpBread.shareEatIt.domain.auth.OAuth2LogoutHandler;
import com.carpBread.shareEatIt.domain.auth.OAuth2Principal;
import com.carpBread.shareEatIt.domain.auth.OAuth2UserService;
import com.carpBread.shareEatIt.domain.auth.util.JWTFilter;
import com.carpBread.shareEatIt.domain.auth.util.JWTUtils;
import com.carpBread.shareEatIt.domain.member.repository.MemberRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

import java.util.Map;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JWTUtils jwtUtils;
    private final MemberRepository memberRepository;

    private final OAuth2UserService oAuth2UserService;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(request-> request
                    .requestMatchers("/login/**").permitAll()
                    .anyRequest().hasRole("MEMBER")
            )
            .oauth2Login(oauth2->
                    oauth2.userInfoEndpoint(o->o.userService(oAuth2UserService))
                            .successHandler(successHandler())
            )
            .addFilterBefore(new JWTFilter(jwtUtils,memberRepository), UsernamePasswordAuthenticationFilter.class)
            .logout(logout -> logout
                    .addLogoutHandler(new OAuth2LogoutHandler())
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

            String token = "Bearer "+jwtUtils.createToken(email, nickname);

            response.setStatus(HttpServletResponse.SC_OK);
            response.setHeader("Authorization",token);
            response.setCharacterEncoding("UTF-8");

            System.out.println(token);

        });
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOrigin("http://localhost:3000");
        configuration.addAllowedOrigin("http://localhost:5173");
        configuration.addAllowedOrigin("http://localhost:5174");
        configuration.addAllowedOrigin("http://localhost:8080");
        configuration.addAllowedOrigin("https://api.dojang-crush.p-e.kr");
        configuration.addAllowedOrigin("https://dojang-crush-client.vercel.app");

        configuration.addAllowedMethod("GET");
        configuration.addAllowedMethod("POST");
        configuration.addAllowedMethod("PATCH");
        configuration.addAllowedMethod("DELETE");
        configuration.addAllowedMethod("OPTIONS");
        configuration.addAllowedHeader("*");
        // 헤더에 authorization항목이 있으므로 credential을 true로 설정합니다.
        configuration.setAllowCredentials(true);

        source.registerCorsConfiguration("/**",configuration);

        return source;
    }
}
