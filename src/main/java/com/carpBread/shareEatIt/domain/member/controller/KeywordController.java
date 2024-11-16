package com.carpBread.shareEatIt.domain.member.controller;

import com.carpBread.shareEatIt.domain.auth.AuthUser;
import com.carpBread.shareEatIt.domain.member.dto.keyword.KeywordAvailableListResponseDto;
import com.carpBread.shareEatIt.domain.member.dto.keyword.KeywordCreateRequestDto;
import com.carpBread.shareEatIt.domain.member.dto.keyword.KeywordResponseDto;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.member.service.KeywordService;
import com.carpBread.shareEatIt.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.print.attribute.standard.MediaSize;

@RestController
@RequestMapping("/keyword")
@RequiredArgsConstructor
public class KeywordController {

    private final KeywordService keywordService;


    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ResponseEntity<ApiResponse<KeywordResponseDto>> createNewKeyword(@AuthUser Member member,
                                                                            @Valid @RequestBody KeywordCreateRequestDto dto){
        KeywordResponseDto responseDto = keywordService.createNewKeyword(member,dto);

        ApiResponse<KeywordResponseDto> response = new ApiResponse<>(HttpStatus.CREATED.value(), "키워드 생성 성공", responseDto);

        return ResponseEntity.ok().body(response);
    }

    // 사용중인(avail==true) 키워드 리스트 조회
    @GetMapping("/avail")
    public ResponseEntity<ApiResponse<KeywordAvailableListResponseDto>> getAllAvailKeywordsList(@AuthUser Member member){
        KeywordAvailableListResponseDto responseDto = keywordService.getAllAvailKeywordList(member);
        ApiResponse<KeywordAvailableListResponseDto> response = new ApiResponse<>(HttpStatus.OK.value(), "현재 사용중인 키워드 목록 조회 성공", responseDto);

        return ResponseEntity.ok().body(response);
    }

    // 등록 기력이 있는 키워드 리스트 조회
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<KeywordAvailableListResponseDto>> getAllKeywordsList(@AuthUser Member member){
        KeywordAvailableListResponseDto responseDto = keywordService.getAllKeywordList(member);
        ApiResponse<KeywordAvailableListResponseDto> response = new ApiResponse<>(HttpStatus.OK.value(), "등록 이력이 있는 키워드 목록 조회 성공", responseDto);

        return ResponseEntity.ok().body(response);
    }

    // 키워드 사용 중지
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<KeywordResponseDto>> changeKeywordUsageToUnAvailable(@AuthUser Member member,
                                                                                           @PathVariable(name = "id") Long id){
        KeywordResponseDto responseDto = keywordService.changeKeywordUsageToUnAvailable(member,id);

        ApiResponse<KeywordResponseDto> response = new ApiResponse<>(HttpStatus.OK.value(), "키워드 사용 여부 수정 성공", responseDto);

        return ResponseEntity.ok().body(response);

    }

    // 키워드 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<KeywordResponseDto>> deleteKeyword(@AuthUser Member member,
                                                                         @PathVariable(name = "id") Long id){
        KeywordResponseDto responseDto = keywordService.deleteKeyword(member,id);

        ApiResponse<KeywordResponseDto> response = new ApiResponse<>(HttpStatus.OK.value(), "키워드 삭제 성공", responseDto);

        return ResponseEntity.ok().body(response);

    }



}





