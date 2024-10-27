package com.carpBread.shareEatIt.domain.report.controller;

import com.carpBread.shareEatIt.domain.auth.AuthUser;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.report.dto.ReportCreateRequestDto;
import com.carpBread.shareEatIt.domain.report.dto.ReportCreateResponseDto;
import com.carpBread.shareEatIt.domain.report.service.ReportService;
import com.carpBread.shareEatIt.domain.sharingPost.dto.SharingPostCreateResponseDto;
import com.carpBread.shareEatIt.global.response.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/report")
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<ApiResponse<ReportCreateResponseDto>> createNewReport(@AuthUser Member member,
                                                                                @NotNull MultipartFile imgFile,
                                                                                @Valid @RequestBody ReportCreateRequestDto dto){
        ReportCreateResponseDto responseDto = reportService.createNewReport(member, imgFile,dto);
        ApiResponse<ReportCreateResponseDto> response = new ApiResponse<>(HttpStatus.CREATED.value(),"나눔글 신고 생성 성공", responseDto);

        return ResponseEntity.ok().body(response);
    }
}
