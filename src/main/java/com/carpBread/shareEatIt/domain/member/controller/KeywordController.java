package com.carpBread.shareEatIt.domain.member.controller;

import com.carpBread.shareEatIt.domain.auth.annotation.AuthUser;
import com.carpBread.shareEatIt.domain.member.dto.keyword.KeywordAvailableListResponseDto;
import com.carpBread.shareEatIt.domain.member.dto.keyword.KeywordCreateRequestDto;
import com.carpBread.shareEatIt.domain.member.dto.keyword.KeywordResponseDto;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.member.service.KeywordService;
import com.carpBread.shareEatIt.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/keyword")
@RequiredArgsConstructor
public class KeywordController {

    private final KeywordService keywordService;


    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ApiResponse<KeywordResponseDto> createNewKeyword(@AuthUser Member member,
                                                                            @Valid @RequestBody KeywordCreateRequestDto dto){
        KeywordResponseDto responseDto = keywordService.createNewKeyword(member,dto);

        ApiResponse<KeywordResponseDto> response = new ApiResponse<>(HttpStatus.CREATED.value(), "키워드 생성 성공", responseDto);

        return response;
    }

    // 사용중인(avail==true) 키워드 리스트 조회
    @GetMapping("/avail")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<KeywordAvailableListResponseDto> getAllAvailKeywordsList(@AuthUser Member member){
        KeywordAvailableListResponseDto responseDto = keywordService.getAllAvailKeywordList(member);
        ApiResponse<KeywordAvailableListResponseDto> response = new ApiResponse<>(HttpStatus.OK.value(), "현재 사용중인 키워드 목록 조회 성공", responseDto);

        return response;
    }

    // 등록 기력이 있는 키워드 리스트 조회
    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<KeywordAvailableListResponseDto> getAllKeywordsList(@AuthUser Member member){
        KeywordAvailableListResponseDto responseDto = keywordService.getAllKeywordList(member);
        ApiResponse<KeywordAvailableListResponseDto> response = new ApiResponse<>(HttpStatus.OK.value(), "등록 이력이 있는 키워드 목록 조회 성공", responseDto);

        return response;
    }

    // 키워드 사용 중지
    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<KeywordResponseDto> changeKeywordUsageToUnAvailable(@AuthUser Member member,
                                                                                           @RequestParam(name = "keyword") String keyword){
        KeywordResponseDto responseDto = keywordService.changeKeywordUsageToUnAvailable(member,keyword);

        ApiResponse<KeywordResponseDto> response = new ApiResponse<>(HttpStatus.OK.value(), "키워드 사용 여부 수정 성공", responseDto);

        return response;

    }

    // 키워드 삭제
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<KeywordResponseDto> deleteKeyword(@AuthUser Member member,
                                                                         @PathVariable(name = "id") Long id){
        KeywordResponseDto responseDto = keywordService.deleteKeyword(member,id);

        ApiResponse<KeywordResponseDto> response = new ApiResponse<>(HttpStatus.OK.value(), "키워드 삭제 성공", responseDto);

        return response;

    }



}





