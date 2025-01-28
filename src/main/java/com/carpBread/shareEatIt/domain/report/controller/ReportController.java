package com.carpBread.shareEatIt.domain.report.controller;

import com.carpBread.shareEatIt.domain.auth.annotation.AuthUser;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.report.dto.ReportCreateRequestDto;
import com.carpBread.shareEatIt.domain.report.dto.ReportCreateResponseDto;
import com.carpBread.shareEatIt.domain.report.service.ReportService;
import com.carpBread.shareEatIt.global.response.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/report")
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    public ApiResponse<ReportCreateResponseDto> createNewReport(@AuthUser Member member,
                                                                                @RequestPart(name = "imgFile") @NotNull MultipartFile imgFile,
                                                                                @RequestPart(name = "dto") @Valid ReportCreateRequestDto dto){
        ReportCreateResponseDto responseDto = reportService.createNewReport(member, imgFile,dto);
        ApiResponse<ReportCreateResponseDto> response = new ApiResponse<>(HttpStatus.CREATED.value(),"나눔글 신고 생성 성공", responseDto);

        return response;
    }
}
