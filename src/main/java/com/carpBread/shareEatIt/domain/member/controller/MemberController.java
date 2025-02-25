package com.carpBread.shareEatIt.domain.member.controller;

import com.carpBread.shareEatIt.domain.auth.annotation.AuthUser;
import com.carpBread.shareEatIt.domain.member.dto.request.MemberProfileUpdateRequestDto;
import com.carpBread.shareEatIt.domain.member.dto.response.*;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.member.service.MemberService;
import com.carpBread.shareEatIt.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/test")
    @ResponseStatus(HttpStatus.OK)
    public String test(@AuthUser Member member){

        System.out.println("===============test=================");
        return "로그인 성공!";
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<MemberProfileResponseDto> memberProfile(@AuthUser Member member){
        Point point=member.getLocationPoint();

        LocationResponseDtoComponent locationResponseDtoComponent = new LocationResponseDtoComponent(
                member.getAddressSt(),
                member.getAddressDetail(),
                point.getY(), point.getX()
        );

        MemberProfileResponseDto responseDto = new MemberProfileResponseDto(
                member.getId(),
                member.getProfileImgUrl(),
                member.getNickname(),
                member.getEmail(),
                locationResponseDtoComponent,
                member.getProvider().name(),
                member.getCreatedAt(),
                member.getModifiedAt()
        );

        ApiResponse response = new ApiResponse<>(HttpStatus.OK.value(),
                "회원 수정페이지 정보 조회 성공",
                responseDto);
        return response;

    }

    @GetMapping("/stickers")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse memberStickers(@AuthUser Member member){
        MemberStickerResponseDto responseDto = memberService.findStickers(member);

        ApiResponse response = new ApiResponse<>(HttpStatus.OK.value(),
                "회원 설정페이지 정보 조회 성공",
                responseDto);

        return response;
    }

    @GetMapping("/sharing/category")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<MemberSharingStatusResponseDto> getMemberSharingStatus(@AuthUser Member member){
        MemberSharingStatusResponseDto responseDto = memberService.findMemberSharingStatus(member);

        ApiResponse response = new ApiResponse(HttpStatus.OK.value(),"회원 나눔 현황 조회 성공", responseDto);

        return response;
    }


    /* 회원 정보 수정 - PUT */
    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse updateMemberProfile(@AuthUser Member member,
                                                           @RequestPart(name = "imgFile",required = false) MultipartFile imgFile,
                                                           @Valid @RequestPart(name = "dto") MemberProfileUpdateRequestDto dto){

        MemberProfileResponseDto responseDto = memberService.updateProfile(member,imgFile, dto);

        ApiResponse response = new ApiResponse<>(HttpStatus.OK.value(),
                "회원 정보 수정 성공",
                responseDto);

        return response;
    }

    @PatchMapping("/avail/keyword")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<AvailResponseDto> updateMemberAvailKeyword(@AuthUser Member member,
                                                                                  @RequestParam(name = "keyword")Boolean keyword){

        AvailResponseDto responseDto = memberService.updateAvailKeyword(member, keyword);
        ApiResponse<AvailResponseDto> response = new ApiResponse<>(HttpStatus.OK.value(),
                "회원 keyword avail 수정",
                responseDto);

        return response;
    }

    @PatchMapping("/avail/notice")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<AvailResponseDto> updateMemberAvail(@AuthUser Member member,
                                                                            @RequestParam(name = "notice") Boolean notice){
        AvailResponseDto responseDto = memberService.updateAvailNotice(member, notice);
        ApiResponse<AvailResponseDto> response = new ApiResponse<>(HttpStatus.OK.value(),
                "회원 notice avail 수정",
                responseDto);

        return response;
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse memberWithdrawal(@AuthUser Member member){
        MemberWithdrawalResponseDto responseDto = memberService.withdrawal(member);

        ApiResponse response = new ApiResponse<>(HttpStatus.OK.value(),
                "회원 탈퇴 성공",
                responseDto);

        return response;
    }


    /* 채팅 - 상대 프로필 조회 */
    @GetMapping("/{opponentId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<OpponentInfoResponseDto> getOpponentInfo(@AuthUser Member member,
                                                                                @PathVariable(name = "opponentId") Long opponentId){
        OpponentInfoResponseDto responseDto = memberService.findOpponentInfo(opponentId);
        ApiResponse response = new ApiResponse<>(HttpStatus.OK.value(),
                "채팅 - 상대 프로필 조회 성공",
                responseDto);

        return response;
    }
}
