package com.carpBread.shareEatIt.domain.member.controller;

import com.carpBread.shareEatIt.domain.member.dto.request.SignUpRequestDto;
import com.carpBread.shareEatIt.domain.member.dto.response.SignUpResponseDto;
import com.carpBread.shareEatIt.domain.member.service.SignUpService;
import com.carpBread.shareEatIt.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/* 회원가입 컨트롤러 */
@RestController
@RequestMapping("/signup")
@RequiredArgsConstructor
public class SignUpController {
    private final SignUpService signUpService;

    @PostMapping
    public ResponseEntity<ApiResponse<SignUpResponseDto>> signUp(@Valid @RequestBody SignUpRequestDto dto){
        SignUpResponseDto responseDto = signUpService.registerNewMember(dto);

        ApiResponse<SignUpResponseDto> response = new ApiResponse<>(
                HttpStatus.CREATED.value(),
                "회원가입이 성공적으로 완료되었습니다.",
                responseDto
        );
        return ResponseEntity.ok().body(response);

    }
}
