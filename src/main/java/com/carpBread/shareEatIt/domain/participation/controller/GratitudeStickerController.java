package com.carpBread.shareEatIt.domain.participation.controller;

import com.carpBread.shareEatIt.domain.auth.AuthUser;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.participation.dto.GratitudeResponseDto;
import com.carpBread.shareEatIt.domain.participation.entity.GratitudeType;
import com.carpBread.shareEatIt.domain.participation.service.GratitudeStickerService;
import com.carpBread.shareEatIt.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/gratitudeStickers")
@RequiredArgsConstructor
public class GratitudeStickerController {

    private final GratitudeStickerService gratitudeStickerService;

    /* 고마움 남기기(생성) */
    @PostMapping("/{postId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse<GratitudeResponseDto>> createGratitudeSticker(@AuthUser Member member,
                                                                                    @PathVariable("postId") Long postId,
                                                                                    @RequestParam("gratitudeType")GratitudeType gratitudeType){
        GratitudeResponseDto responseDto = gratitudeStickerService.createGratitudeSticker(postId, member, gratitudeType);
        ApiResponse<GratitudeResponseDto> response = new ApiResponse<>(
                HttpStatus.CREATED.value(),  // 상태코드 201
                "고마움 생성 성공",   // 성공 메시지
                responseDto      // 실제 데이터
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /* 고마움 수정하기 */
    @PatchMapping("/{gsId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse<GratitudeResponseDto>> updateGratitudeSticker(@AuthUser Member member,
                                                                                    @PathVariable("gsId") Long gratitudeStickerId,
                                                                                    @RequestParam("gratitudeType")GratitudeType gratitudeType){
        GratitudeResponseDto responseDto = gratitudeStickerService.updateGratitudeStickers(gratitudeStickerId, member, gratitudeType);
        ApiResponse<GratitudeResponseDto> response = new ApiResponse<>(
                HttpStatus.OK.value(),  //상태코드 200
                "고마움 스티커 수정 성공",  // 성공 메시지
                responseDto
        );
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
