package com.carpBread.shareEatIt.domain.participation.controller;

import com.carpBread.shareEatIt.domain.participation.dto.*;
import com.carpBread.shareEatIt.domain.participation.entity.ParticipationStatus;
import com.carpBread.shareEatIt.domain.participation.service.ParticipationService;
import com.carpBread.shareEatIt.domain.sharingPost.entity.PostType;
import com.carpBread.shareEatIt.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/participations")
@RequiredArgsConstructor
public class ParticipationController {

    private final ParticipationService participationService;

    /* 로그인 구현 전 - 모든 기본 경로 뒤에 receiverId 붙여서 구현함 */

    // 참여 생성 - 로그인 구현 전
    @PostMapping("/{receiverId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse<ParticipationResponseDto>> createParticipation(@PathVariable("receiverId") Long receiverId,
                                                                                    @RequestBody @Valid final ParticipationRequestDto dto) {
        ParticipationResponseDto responseDto = participationService.createParticipation(receiverId, dto);
        ApiResponse<ParticipationResponseDto> response = new ApiResponse<>(
                HttpStatus.CREATED.value(),  // 상태코드 201
                "참여 생성 성공",   // 성공 메시지
                responseDto      // 실제 데이터
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    // 사용자의 나눔 참여 목록 전체 조회
    @GetMapping("/{receiverId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse<ParticipationHistoryListResponseDto>> getAllParticipation(@PathVariable("receiverId") Long receiverId){
        ParticipationHistoryListResponseDto responseDto = participationService.findAllParticipation(receiverId);
        ApiResponse<ParticipationHistoryListResponseDto> response = new ApiResponse<>(
                HttpStatus.OK.value(),  // 상태코드 200
                "사용자의 나눔 참여 목록 전체 조회 성공",   // 성공 메시지
                responseDto      // 실제 데이터
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    // 해당 제공 주체의 나눔 참여 목록 조회
    @GetMapping("/{provider}/{receiverId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse<ParticipationHistoryListResponseDto>> getParticipationByProvider(@PathVariable("receiverId") Long receiverId,
                                                                                                       @PathVariable("provider") PostType provider){
        ParticipationHistoryListResponseDto responseDto = participationService.findAllParticipationByProvider(receiverId,provider);
        ApiResponse<ParticipationHistoryListResponseDto> response = new ApiResponse<>(
                HttpStatus.OK.value(),  // 상태코드 200
                "사용자의 나눔 참여 목록 중 특정 제공자의 나눔 참여 목록 조회 성공",   // 성공 메시지
                responseDto      // 실제 데이터
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


}
