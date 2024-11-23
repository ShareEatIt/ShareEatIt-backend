package com.carpBread.shareEatIt.domain.member.controller;

import com.carpBread.shareEatIt.domain.auth.AuthUser;
import com.carpBread.shareEatIt.domain.member.dto.*;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.member.service.MemberService;
import com.carpBread.shareEatIt.domain.notice.controller.NoticeController;
import com.carpBread.shareEatIt.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.metamodel.model.domain.internal.MapMember;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.service.annotation.GetExchange;

import java.awt.*;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final GeometryFactory geometryFactory = new GeometryFactory();

    @GetMapping("/test")
    public ResponseEntity<String> test(@AuthUser Member member){

        System.out.println(member.getEmail());

        return ResponseEntity.ok("로그인 성공!");
    }

    @GetMapping
    public ResponseEntity<ApiResponse<MemberProfileResponseDto>> memberProfile(@AuthUser Member member){
        Point point;
        if (member.getLocationPoint()==null){
            point= geometryFactory.createPoint(new Coordinate(127.02-90.0, 37.63-90.0));
        }else{
            point=member.getLocationPoint();
        }

        MemberProfileResponseDto responseDto = MemberProfileResponseDto.builder()
                .id(member.getId())
                .profileImg(member.getProfileImgUrl())
                .nickname(member.getNickname())
                .email(member.getEmail())
                .location(LocationResponseDtoComponent.builder()
                        .addressSt(member.getAddressSt())
                        .addressDetail(member.getAddressDetail())
                        .latitude(point.getY())
                        .longitude(point.getX())
                        .build())
                .provider(member.getProvider().name())
                .joinedAt(member.getCreatedAt())
                .recentModifiedAt(member.getModifiedAt())
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

    @GetMapping("/sharing/category")
    public ResponseEntity<ApiResponse<MemberSharingStatusResponseDto>> getMemberSharingStatus(@AuthUser Member member){
        MemberSharingStatusResponseDto responseDto = memberService.findMemberSharingStatus(member);

        ApiResponse response = new ApiResponse(HttpStatus.OK.value(),"회원 나눔 현황 조회 성공", responseDto);

        return ResponseEntity.ok().body(response);
    }


    @PutMapping
    public ResponseEntity<ApiResponse> updateMemberProfile(@AuthUser Member member,
                                                           @RequestPart(name = "imgFile",required = false) MultipartFile imgFile,
                                                           @Valid @RequestPart(name = "dto") MemberProfileUpdateRequestDto dto){

        MemberProfileResponseDto responseDto = memberService.updateProfile(member.getId(),imgFile, dto);

        ApiResponse response = new ApiResponse<>(HttpStatus.OK.value(),
                "회원 정보 수정 성공",
                responseDto);

        return ResponseEntity.ok().body(response);
    }

    @PatchMapping("/avail/keyword")
    public ResponseEntity<ApiResponse<AvailResponseDto>> updateMemberAvailKeyword(@AuthUser Member member,
                                                                                          @RequestParam(name = "keyword")Boolean keyword){

        AvailResponseDto responseDto = memberService.updateAvailKeyword(member, keyword);
        ApiResponse<AvailResponseDto> response = new ApiResponse<>(HttpStatus.OK.value(),
                "회원 keyword avail 수정",
                responseDto);

        return ResponseEntity.ok().body(response);
    }

    @PatchMapping("/avail/notice")
    public ResponseEntity<ApiResponse<AvailResponseDto>> updateMemberAvail(@AuthUser Member member,
                                                         @RequestParam(name = "notice") Boolean notice){
        AvailResponseDto responseDto = memberService.updateAvailNotice(member, notice);
        ApiResponse<AvailResponseDto> response = new ApiResponse<>(HttpStatus.OK.value(),
                "회원 notice avail 수정",
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
