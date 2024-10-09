package com.carpBread.shareEatIt.domain.member.controller;

import com.carpBread.shareEatIt.domain.auth.AuthUser;
import com.carpBread.shareEatIt.domain.auth.OAuth2Principal;
import com.carpBread.shareEatIt.domain.member.dto.*;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.member.service.MemberService;
import com.carpBread.shareEatIt.global.config.SecurityConfig;
import com.carpBread.shareEatIt.global.response.ApiResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/test")
    public ResponseEntity<String> test(@AuthUser Member member){

        System.out.println(member.getEmail());

        return ResponseEntity.ok("로그인 성공!");
    }

    @GetMapping
    public ResponseEntity<ApiResponse> memberProfile(@AuthUser Member member){
        MemberProfileResponseDto responseDto = MemberProfileResponseDto.builder()
                .profileImg(member.getProfileImgUrl())
                .nickname(member.getNickname())
                .email(member.getEmail())
                .location(LocationResponseDto.builder()
                        .addressSt(member.getAddressSt())
                        .addressDetail(member.getAddressDetail())
                        .build())
                .build();

        ApiResponse response = new ApiResponse<>(HttpStatus.OK.value(),
                "회원 수정페이지 정보 조회 성공",
                responseDto);
        return ResponseEntity.ok().body(response);

    }

    @GetMapping("/stickers")
    public ResponseEntity<ApiResponse> memberStickers(@AuthUser Member member){
        MemberStickerResponseDto responseDto = memberService.findStickers(member.getId());

        ApiResponse response = new ApiResponse<>(HttpStatus.OK.value(),
                "회원 설정페이지 정보 조회 성공",
                responseDto);

        return ResponseEntity.ok().body(response);
    }

    @PutMapping
    public ResponseEntity<ApiResponse> updateMemberProfile(@AuthUser Member member,
                                                           @RequestBody MemberProfileUpdateRequestDto requestDto){

        MemberProfileResponseDto responseDto = memberService.updateProfile(member.getId(), requestDto);

        ApiResponse response = new ApiResponse<>(HttpStatus.OK.value(),
                "회원 정보 수정 성공",
                responseDto);

        return ResponseEntity.ok().body(response);
    }

    @PatchMapping("/avail")
    public ResponseEntity<ApiResponse> updateMemberAvail(@AuthUser Member member,
                                                         @RequestBody MemberAvailRequestDto dto){
        MemberStickerResponseDto responseDto = memberService.updateAvail(dto,member.getId());
        ApiResponse response = new ApiResponse<>(HttpStatus.OK.value(),
                "회원 keyword avail, notice avail 수정",
                responseDto);

        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse> memberWithdrawal(@AuthUser Member member){
        MemberWithdrawalResponseDto responseDto = memberService.withdrawal(member.getId());

        ApiResponse response = new ApiResponse<>(HttpStatus.OK.value(),
                "회원 탈퇴 성공",
                responseDto);

        return ResponseEntity.ok().body(response);
    }

}
