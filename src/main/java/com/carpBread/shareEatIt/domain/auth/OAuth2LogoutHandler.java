package com.carpBread.shareEatIt.domain.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

public class OAuth2LogoutHandler implements LogoutHandler {

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    String clientId;
    private final String logoutRedirectUri = "http://localhost:8080/logout";

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String token = request.getHeader("Authorization").split(" ")[1];
        System.out.println(token);

    }
}
