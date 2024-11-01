package com.carpBread.shareEatIt.domain.sharingPost.controller;

import com.carpBread.shareEatIt.domain.auth.AuthUser;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.sharingPost.dto.SharingPostResponseDto;
import com.carpBread.shareEatIt.domain.sharingPost.dto.map.MapListResponseDto;
import com.carpBread.shareEatIt.domain.sharingPost.dto.map.MapRequestDto;
import com.carpBread.shareEatIt.domain.sharingPost.service.SharingPostService;
import com.carpBread.shareEatIt.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/map")
public class MapController {

    private final SharingPostService sharingPostService;

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<MapListResponseDto>> getMapList(@AuthUser Member member,
                                                                      @Valid @RequestBody MapRequestDto dto){
        MapListResponseDto responseDto = sharingPostService.getMapList(member,dto);

        ApiResponse<MapListResponseDto> response = new ApiResponse<>(HttpStatus.OK.value(),"지도 나눔글 목록 조회 성공", responseDto);

        return ResponseEntity.ok().body(response);
    }
}
