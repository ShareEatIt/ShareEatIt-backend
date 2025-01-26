package com.carpBread.shareEatIt.domain.notice.controller;

import com.carpBread.shareEatIt.domain.auth.annotation.AuthUser;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.notice.dto.NoticeListResponseDto;
import com.carpBread.shareEatIt.domain.notice.dto.NoticeResponseDto;
import com.carpBread.shareEatIt.domain.notice.service.NoticeService;
import com.carpBread.shareEatIt.domain.notice.service.SseService;
import com.carpBread.shareEatIt.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final SseService sseService;
    private final NoticeService noticeService;

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<NoticeListResponseDto>> findNoticeList(@AuthUser Member member){
        NoticeListResponseDto responseDto = noticeService.findUnreadNoticeList(member);

        ApiResponse<NoticeListResponseDto> response = new ApiResponse<>(HttpStatus.OK.value(),"알림 목록 조회 성공", responseDto);

        return ResponseEntity.ok().body(response);

    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<NoticeResponseDto>> findNotice(@AuthUser Member member,
                                                                     @PathVariable(name = "id") Long id){
        NoticeResponseDto responseDto = noticeService.findNoticeById(member, id);
        ApiResponse<NoticeResponseDto> response = new ApiResponse<>(HttpStatus.OK.value(), "알림 조회 성공", responseDto);
        return ResponseEntity.ok().body(response);
    }

    // 클라이언트가 서버에 연결될 때
    @GetMapping("/subscribe")
    public SseEmitter subscribe(@AuthUser Member member){
        SseEmitter sseEmitter = new SseEmitter();
        if (!sseService.isRegistered(member.getId()))
            sseService.registerClient(member.getId());

        return sseEmitter;

    }

    @GetMapping("/isOnList")
    public Boolean isOnList(@AuthUser Member member){
        return sseService.isRegistered(member.getId());
    }


}
