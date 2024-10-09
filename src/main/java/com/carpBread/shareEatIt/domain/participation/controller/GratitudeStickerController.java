package com.carpBread.shareEatIt.domain.participation.controller;

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
    @PostMapping("/{postId}/{memberId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse<GratitudeResponseDto>> createGratitudeSticker(@PathVariable("postId") Long postId,
                                                                                    @PathVariable("memberId") Long memberId,
                                                                                    @RequestParam("gratitudeType")GratitudeType gratitudeType){
        GratitudeResponseDto responseDto = gratitudeStickerService.createGratitudeSticker(postId, memberId, gratitudeType);
        ApiResponse<GratitudeResponseDto> response = new ApiResponse<>(
                HttpStatus.CREATED.value(),  // 상태코드 201
                "고마움 생성 성공",   // 성공 메시지
                responseDto      // 실제 데이터
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
