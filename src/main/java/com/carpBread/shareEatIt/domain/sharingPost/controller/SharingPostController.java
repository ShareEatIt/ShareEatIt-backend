package com.carpBread.shareEatIt.domain.sharingPost.controller;

import com.carpBread.shareEatIt.domain.sharingPost.dto.SharingPostCreateRequestDto;
import com.carpBread.shareEatIt.domain.sharingPost.dto.SharingPostCreateResponseDto;
import com.carpBread.shareEatIt.domain.sharingPost.service.SharingPostService;
import com.carpBread.shareEatIt.global.response.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sharing")
public class SharingPostController {

    private final SharingPostService sharingPostService;

    @PostMapping
    public ResponseEntity<ApiResponse> createNewSharingPost(@NotNull List<MultipartFile> imgList,
                                                            @Valid @RequestBody SharingPostCreateRequestDto dto){

        SharingPostCreateResponseDto responseDto = sharingPostService.createSharingPost(imgList,dto);

        ApiResponse<SharingPostCreateResponseDto> response = new ApiResponse<>(HttpStatus.CREATED.value(),"나눔글 생성 성공", responseDto);

        return ResponseEntity.ok().body(response);
    }


}
