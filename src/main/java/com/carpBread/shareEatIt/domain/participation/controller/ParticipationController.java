package com.carpBread.shareEatIt.domain.participation.controller;

import com.carpBread.shareEatIt.domain.auth.annotation.AuthUser;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.participation.dto.responseDto.ParticipationHistoryListResponseDto;
import com.carpBread.shareEatIt.domain.participation.dto.requestDto.ParticipationRequestDto;
import com.carpBread.shareEatIt.domain.participation.dto.responseDto.ParticipationResponseDto;
import com.carpBread.shareEatIt.domain.participation.dto.responseDto.ParticipationUpdateStatusResponseDto;
import com.carpBread.shareEatIt.domain.participation.entity.ParticipationStatus;
import com.carpBread.shareEatIt.domain.participation.service.ParticipationService;
import com.carpBread.shareEatIt.domain.sharingPost.entity.PostType;
import com.carpBread.shareEatIt.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/participations")
@RequiredArgsConstructor
public class ParticipationController {

    private final ParticipationService participationService;

    /* 참여 생성 */
    @PostMapping()
    // 경우에 따라 CREATED or OK의 두가지 중 하나의 상태를 가지므로 ResponseEntity 사용
    public ResponseEntity<ApiResponse<ParticipationResponseDto>> createParticipation(@AuthUser Member receiver,
                                                                                     @RequestBody @Valid final ParticipationRequestDto requestDto) {
        Pair<HttpStatus, ParticipationResponseDto> result = participationService.createParticipation(receiver, requestDto);
        String message = result.getLeft() == HttpStatus.CREATED ? "참여 생성 성공." : "기존 참여 정보 반환";

        ApiResponse<ParticipationResponseDto> response = new ApiResponse<>(
                result.getLeft().value(),  // 상태코드 201 or 200
                message,                   // 성공 메시지
                result.getRight()          // 실제 데이터
        );
        return new ResponseEntity<>(response, result.getLeft());
    }


    /* 사용자의 나눔 참여 목록 전체 조회 */
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


    /* 해당 제공 주체의 나눔 참여 목록 조회 */
    @GetMapping("/{provider}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<ParticipationHistoryListResponseDto> getParticipationByProvider(@AuthUser Member receiver,
                                                                                       @PathVariable("provider") PostType provider){
        ParticipationHistoryListResponseDto responseDto = participationService.findAllParticipationByProvider(receiver,provider);
        return new ApiResponse<>(
                HttpStatus.OK.value(),  // 상태코드 200
                "사용자의 나눔 참여 목록 중 특정 제공자의 나눔 참여 목록 조회 성공",   // 성공 메시지
                responseDto      // 실제 데이터
        );
    }


    /* 참여 테이블의 참여 상태 변경 */
    @PatchMapping("/{ptId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<ParticipationUpdateStatusResponseDto> updateStatus(@AuthUser Member giver,
                                                                          @PathVariable("ptId") Long ptId,
                                                                          @RequestParam("status") ParticipationStatus status){
        ParticipationUpdateStatusResponseDto responseDto = participationService.updateStatus(ptId, giver, status);
        return new ApiResponse<>(
                HttpStatus.OK.value(),  // 상태코드 200
                "참여 상태 수정 성공",   // 성공 메시지
                responseDto      // 실제 데이터
        );
    }

}
