package com.carpBread.shareEatIt.domain.member.controller;

import com.carpBread.shareEatIt.domain.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/sentry")
@RequiredArgsConstructor
public class SentryTestController {

    private final MemberService memberService;

    @Getter
    @NoArgsConstructor
    public static class SentrySampleDto{
        private String name;
        private Long id;
    }

    @GetMapping
    public ResponseEntity<?> sentryTest(HttpServletRequest request, @RequestBody SentrySampleDto dto){

        String returnValue= memberService.sentryTest(dto);

        return ResponseEntity.ok(returnValue);
    }
}
