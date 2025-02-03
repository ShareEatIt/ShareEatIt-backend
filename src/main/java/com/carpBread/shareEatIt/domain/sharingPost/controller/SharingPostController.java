package com.carpBread.shareEatIt.domain.sharingPost.controller;

import com.carpBread.shareEatIt.domain.auth.annotation.AuthUser;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.sharingPost.dto.*;
import com.carpBread.shareEatIt.domain.sharingPost.service.CreateSharingPostService;
import com.carpBread.shareEatIt.domain.sharingPost.service.SharingPostReadService;
import com.carpBread.shareEatIt.domain.sharingPost.service.SharingPostService;
import com.carpBread.shareEatIt.global.response.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/* 나눔글 CRUD controller */
@RestController
@RequiredArgsConstructor
@RequestMapping("/sharing")
public class SharingPostController {

    private final SharingPostService sharingPostService;
    private final SharingPostReadService sharingPostReadService;
    private final CreateSharingPostService createSharingPostService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<SharingPostCreateResponseDto> createNewSharingPost(@AuthUser Member member,
                                                                                          @RequestPart(name = "imgList") @NotNull List<MultipartFile> imgList,
                                                                                          @RequestPart(name = "dto") @Valid SharingPostRequestDto dto){
        SharingPostCreateResponseDto responseDto = createSharingPostService.createSharingPost(imgList,dto,member);
        ApiResponse<SharingPostCreateResponseDto> response = new ApiResponse<>(HttpStatus.CREATED.value(),"나눔글 생성 성공", responseDto);

        return response;
    }

    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<SharingPostListResponseDto> findSharingPostListByProviderType(@AuthUser Member member,
                                                                                                     @NotBlank @RequestParam(name = "postType")String postType,
                                                                                                     @NotNull @RequestParam(name = "latitude")Double latitude,
                                                                                                     @NotNull @RequestParam(name = "longitude")Double longitude){

        SharingPostListRequestDto dto = new SharingPostListRequestDto(postType, latitude, longitude);
        SharingPostListResponseDto responseDto = sharingPostReadService.findPostListByProviderType(dto);
        ApiResponse<SharingPostListResponseDto> response = new ApiResponse<>(HttpStatus.OK.value(),"나눔글 리스트 조회 성공", responseDto);

        return response;

    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<SharingPostResponseDto> findSharingPostById(@AuthUser Member member,
                                                                                   @PathVariable(name = "id")Long id){
        SharingPostResponseDto responseDto = sharingPostReadService.findSharingPostByID(member, id);
        ApiResponse<SharingPostResponseDto> response = new ApiResponse<>(HttpStatus.OK.value(),"나눔글 상세 조회 성공", responseDto);

        return response;

    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<SharingPostResponseDto> updateSharingPost(@AuthUser Member member,
                                                                                 @PathVariable(name = "id")Long id,
                                                                                 @RequestPart(name = "imgList", required = false) List<MultipartFile> imgList,
                                                                                 @RequestPart(name = "dto") SharingPostUpdateRequestDto dto){
        SharingPostResponseDto responseDto = sharingPostService.updateSharingPost(member,id, imgList, dto);
        ApiResponse<SharingPostResponseDto> response = new ApiResponse<>(HttpStatus.OK.value(),"나눔글 정보 수정 성공", responseDto);

        return response;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Long> deleteSharingPost(@AuthUser Member member,
                                                               @PathVariable(name = "id")Long id){

        sharingPostService.deleteSharingPost(member,id);
        ApiResponse<Long> response = new ApiResponse<>(HttpStatus.OK.value(),"나눔글 삭제 성공", id);

        return response;

    }
}
