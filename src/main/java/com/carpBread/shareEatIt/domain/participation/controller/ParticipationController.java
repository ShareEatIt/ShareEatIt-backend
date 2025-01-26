package com.carpBread.shareEatIt.domain.participation.controller;

import com.carpBread.shareEatIt.domain.auth.AuthUser;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.participation.dto.ParticipationHistoryListResponseDto;
import com.carpBread.shareEatIt.domain.participation.dto.ParticipationRequestDto;
import com.carpBread.shareEatIt.domain.participation.dto.ParticipationResponseDto;
import com.carpBread.shareEatIt.domain.participation.dto.ParticipationUpdateStatusResponseDto;
import com.carpBread.shareEatIt.domain.participation.entity.ParticipationStatus;
import com.carpBread.shareEatIt.domain.participation.service.ParticipationService;
import com.carpBread.shareEatIt.domain.sharingPost.entity.PostType;
import com.carpBread.shareEatIt.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/participations")
@RequiredArgsConstructor
public class ParticipationController {

    private final ParticipationService participationService;

    // 참여 생성
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ParticipationResponseDto> createParticipation(@AuthUser Member receiver,
                                                                                     @RequestBody @Valid final ParticipationRequestDto dto) {
        ParticipationResponseDto responseDto = participationService.createParticipation(receiver, dto);
        ApiResponse<ParticipationResponseDto> response = new ApiResponse<>(
                HttpStatus.CREATED.value(),  // 상태코드 201
                "참여 생성 성공",   // 성공 메시지
                responseDto      // 실제 데이터
        );
        return response;
    }


    // 사용자의 나눔 참여 목록 전체 조회
    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<ParticipationHistoryListResponseDto> getAllParticipation(@AuthUser Member receiver){
        ParticipationHistoryListResponseDto responseDto = participationService.findAllParticipation(receiver);
        ApiResponse<ParticipationHistoryListResponseDto> response = new ApiResponse<>(
                HttpStatus.OK.value(),  // 상태코드 200
                "사용자의 나눔 참여 목록 전체 조회 성공",   // 성공 메시지
                responseDto      // 실제 데이터
        );
        return response;
    }


    // 해당 제공 주체의 나눔 참여 목록 조회
    @GetMapping("/{provider}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<ParticipationHistoryListResponseDto> getParticipationByProvider(@AuthUser Member receiver,
                                                                                                       @PathVariable("provider") PostType provider){
        ParticipationHistoryListResponseDto responseDto = participationService.findAllParticipationByProvider(receiver,provider);
        ApiResponse<ParticipationHistoryListResponseDto> response = new ApiResponse<>(
                HttpStatus.OK.value(),  // 상태코드 200
                "사용자의 나눔 참여 목록 중 특정 제공자의 나눔 참여 목록 조회 성공",   // 성공 메시지
                responseDto      // 실제 데이터
        );
        return response;
    }


    // 참여 테이블의 참여 상태 변경
    @PatchMapping("/{ptId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<ParticipationUpdateStatusResponseDto> updateStatus(@AuthUser Member giver,
                                                                                          @PathVariable("ptId") Long ptId,
                                                                                          @RequestParam("status") ParticipationStatus status){
        ParticipationUpdateStatusResponseDto responseDto = participationService.updateStatus(ptId, giver, status);
        ApiResponse<ParticipationUpdateStatusResponseDto> response = new ApiResponse<>(
                HttpStatus.OK.value(),  // 상태코드 200
                "참여 상태 수정 성공",   // 성공 메시지
                responseDto      // 실제 데이터
        );
        return response;
    }

}
