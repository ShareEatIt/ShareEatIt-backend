package com.carpBread.shareEatIt.domain.sharingPost.controller;

import com.carpBread.shareEatIt.domain.auth.AuthUser;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.sharingPost.dto.*;
import com.carpBread.shareEatIt.domain.sharingPost.service.SharingPostService;
import com.carpBread.shareEatIt.global.response.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sharing")
public class SharingPostController {

    private final SharingPostService sharingPostService;

    @PostMapping
    public ResponseEntity<ApiResponse<SharingPostCreateResponseDto>> createNewSharingPost(@AuthUser Member member,
                                                                                          @RequestPart(name = "imgList") @NotNull List<MultipartFile> imgList,
                                                                                          @RequestPart(name = "dto") @Valid SharingPostRequestDto dto){


        SharingPostCreateResponseDto responseDto = sharingPostService.createSharingPost(imgList,dto,member);

        ApiResponse<SharingPostCreateResponseDto> response = new ApiResponse<>(HttpStatus.CREATED.value(),"나눔글 생성 성공", responseDto);

        return ResponseEntity.ok().body(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<SharingPostListResponseDto>> findSharingPostListByProviderType(@AuthUser Member member,
                                                                  @Valid @RequestBody SharingPostListRequestDto dto){
        SharingPostListResponseDto responseDto = sharingPostService.findPostListByProviderType(member, dto);

        ApiResponse<SharingPostListResponseDto> response = new ApiResponse<>(HttpStatus.OK.value(),"나눔글 리스트 조회 성공", responseDto);

        return ResponseEntity.ok().body(response);

    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SharingPostResponseDto>> findSharingPostById(@AuthUser Member member,
                                                                                   @PathVariable(name = "id")Long id){
        SharingPostResponseDto responseDto = sharingPostService.findSharingPostByID(member, id);

        ApiResponse<SharingPostResponseDto> response = new ApiResponse<>(HttpStatus.OK.value(),"나눔글 상세 조회 성공", responseDto);

        return ResponseEntity.ok().body(response);

    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SharingPostResponseDto>> updateSharingPost(@AuthUser Member member,
                                                                                 @PathVariable(name = "id")Long id,
                                                                                 @RequestPart(name = "imgList", required = false) List<MultipartFile> imgList,
                                                                                 @RequestPart(name = "dto") SharingPostUpdateRequestDto dto){
        SharingPostResponseDto responseDto = sharingPostService.updateSharingPost(member,id, imgList, dto);
        ApiResponse<SharingPostResponseDto> response = new ApiResponse<>(HttpStatus.OK.value(),"나눔글 정보 수정 성공", responseDto);

        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Long>> deleteSharingPost(@AuthUser Member member,
                                                               @PathVariable(name = "id")Long id){

        sharingPostService.deleteSharingPost(member,id);

        ApiResponse<Long> response = new ApiResponse<>(HttpStatus.OK.value(),"나눔글 삭제 성공", id);

        return ResponseEntity.ok().body(response);

    }





}
