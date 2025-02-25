package com.carpBread.shareEatIt.domain.sharingPost.controller;

import com.carpBread.shareEatIt.domain.auth.annotation.AuthUser;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.sharingPost.dto.request.SharingStatsDetailRequestDto;
import com.carpBread.shareEatIt.domain.sharingPost.dto.response.stats.RankResponseDto;
import com.carpBread.shareEatIt.domain.sharingPost.dto.response.stats.SharingPostStatsPeriodResponseDto;
import com.carpBread.shareEatIt.domain.sharingPost.dto.response.stats.SharingStatsPeriodCurrentResponseDto;
import com.carpBread.shareEatIt.domain.sharingPost.service.StatsService;
import com.carpBread.shareEatIt.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/* 개인 나눔 통계 api controller */
@RestController
@RequiredArgsConstructor
@RequestMapping("/sharing/stats")
public class SharingPostStatisticsController {

    private final StatsService statsService;

    @GetMapping("/period/detail")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<SharingPostStatsPeriodResponseDto> getStatsByPeriodInDetail(@AuthUser Member member,
                                                                           @RequestBody @Valid SharingStatsDetailRequestDto dto){

        SharingPostStatsPeriodResponseDto responseDto = statsService.getStatsByPeriod(member,dto);
        return new ApiResponse<SharingPostStatsPeriodResponseDto>(HttpStatus.OK.value(),"회원 나눔글 상세 통계 조회 성공",responseDto);
    }

    @GetMapping("/period/current")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<SharingStatsPeriodCurrentResponseDto> getCurrentStats(@AuthUser Member member){
        SharingStatsPeriodCurrentResponseDto responseDto = statsService.getCurrentStats(member);
        return new ApiResponse<SharingStatsPeriodCurrentResponseDto>(
                HttpStatus.OK.value(),
                "현재 월별, 주별 회원 나눔글 작성수 조회 성공",
                responseDto);
    }

    @GetMapping("/rank")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<RankResponseDto> getSharingRankOfWriter(@AuthUser Member member){
        RankResponseDto responseDto = statsService.sharingRankOfWriter(member);
        return new ApiResponse<>(HttpStatus.OK.value(), "회원 나눔 총 순위", responseDto);
    }
}
