package com.carpBread.shareEatIt.domain.member.controller;

import com.carpBread.shareEatIt.domain.auth.AuthUser;
import com.carpBread.shareEatIt.domain.auth.OAuth2Principal;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.global.config.SecurityConfig;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/members")
public class MemberController {

    @GetMapping
    public ResponseEntity<String> test(@AuthUser Member member){

        System.out.println(member.getEmail());

        return ResponseEntity.ok("로그인 성공!");
    }
}
