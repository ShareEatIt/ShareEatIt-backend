package com.carpBread.shareEatIt.domain.sharingPost.controller;

import com.carpBread.shareEatIt.domain.auth.annotation.AuthUser;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.sharingPost.dto.response.stats.RankResponseDto;
import com.carpBread.shareEatIt.domain.sharingPost.dto.response.stats.SharingPostStatsPeriodResponseDto;
import com.carpBread.shareEatIt.domain.sharingPost.service.StatsService;
import com.carpBread.shareEatIt.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/* 개인 나눔 통계 api controller */
@RestController
@RequiredArgsConstructor
@RequestMapping("/sharing/stats")
public class SharingPostStatisticsController {

    private final StatsService statsService;

    @GetMapping("/period/{periodType}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<SharingPostStatsPeriodResponseDto> getStatsByPeriod(@AuthUser Member member,
                                                                           @PathVariable(name = "periodType")String periodType){

        SharingPostStatsPeriodResponseDto responseDto = statsService.getStatsByPeriod(member,periodType);
        return new ApiResponse<SharingPostStatsPeriodResponseDto>(HttpStatus.OK.value(),"회원 나눔글 통계 조회 성공",responseDto);
    }

    @GetMapping("/rank")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<RankResponseDto> getSharingRankOfWriter(@AuthUser Member member){
        RankResponseDto responseDto = statsService.sharingRankOfWriter(member);
        return new ApiResponse<>(HttpStatus.OK.value(), "회원 나눔 총 순위", responseDto);
    }
}
