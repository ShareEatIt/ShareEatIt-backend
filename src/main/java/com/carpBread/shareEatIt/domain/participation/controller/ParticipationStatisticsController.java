package com.carpBread.shareEatIt.domain.participation.controller;

import com.carpBread.shareEatIt.domain.auth.annotation.AuthUser;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.participation.dto.stats.ParticipationStatsPeriodResponseDto;
import com.carpBread.shareEatIt.domain.participation.service.ParticipationStatsService;
import com.carpBread.shareEatIt.domain.sharingPost.dto.response.stats.RankResponseDto;
import com.carpBread.shareEatIt.global.response.ApiResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/* 개인 참여 통계 api controller */
@RestController
@RequiredArgsConstructor
@RequestMapping("/participation/stats")
public class ParticipationStatisticsController {

    private final ParticipationStatsService participationStatsService;


    // 기간 내 개인 참여 통계 조회
    @GetMapping("/period/{periodType}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<ParticipationStatsPeriodResponseDto> getStatsByPeriod(@AuthUser Member member,
                                                                             @PathVariable(name = "periodType")String periodType){
        ParticipationStatsPeriodResponseDto responseDto = participationStatsService.getStatsByPeriod(member, periodType);
        return new ApiResponse<>(HttpStatus.OK.value(), "회원 참여 통계 조회 성공", responseDto);

    }

    // 모든 사용자 중 참여 순위
    @GetMapping("/rank")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<RankResponseDto> getParticipationRankOfReceiver(@AuthUser Member member){
        RankResponseDto responseDto = participationStatsService.participationRankOfReceiver(member);
        return new ApiResponse<>(HttpStatus.OK.value(), "회원 참여 총 순위 조회 성공", responseDto);
    }

}
