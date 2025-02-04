package com.carpBread.shareEatIt.domain.sharingPost.controller;

import com.carpBread.shareEatIt.domain.auth.annotation.AuthUser;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.sharingPost.dto.map.MapListResponseDto;
import com.carpBread.shareEatIt.domain.sharingPost.dto.map.MapRequestDto;
import com.carpBread.shareEatIt.domain.sharingPost.service.MapService;
import com.carpBread.shareEatIt.global.response.ApiResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/map")
public class MapController {

    private final MapService mapService;

    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<MapListResponseDto> getMapList(@AuthUser Member member,
                                                                      @NotNull @RequestParam(name = "longitude")Double longitude,
                                                                      @NotNull @RequestParam(name = "latitude")Double latitude){
        MapRequestDto dto = new MapRequestDto(longitude, latitude);


        MapListResponseDto responseDto = mapService.getMapList(dto);

        ApiResponse<MapListResponseDto> response = new ApiResponse<>(HttpStatus.OK.value(),"지도 나눔글 목록 조회 성공", responseDto);

        return response;
    }
}
