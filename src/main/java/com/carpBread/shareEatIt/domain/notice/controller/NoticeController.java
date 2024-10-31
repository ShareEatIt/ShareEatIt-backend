package com.carpBread.shareEatIt.domain.notice.controller;

import com.carpBread.shareEatIt.domain.auth.AuthUser;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.notice.dto.NoticeListResponseDto;
import com.carpBread.shareEatIt.domain.notice.service.NoticeService;
import com.carpBread.shareEatIt.domain.sharingPost.dto.SharingPostResponseDto;
import com.carpBread.shareEatIt.global.response.ApiResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

@RestController
@RequestMapping("/notice")
@RequiredArgsConstructor
public class NoticeController {

    private static Map<Long, SseEmitter> clients = new ConcurrentHashMap<>();

    public static void putMemberToClients(Long memberId){
        clients.put(memberId,new SseEmitter());
    }

    public static void removeMemberFromClients(Long memberId){
        clients.remove(memberId);
    }

    public static SseEmitter getSseEmitterByMemberId(Long memberId){
        return clients.get(memberId);
    }

    private final NoticeService noticeService;

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<NoticeListResponseDto>> findNoticeList(@AuthUser Member member){
        NoticeListResponseDto responseDto = noticeService.findUnreadNoticeList(member);

        ApiResponse<NoticeListResponseDto> response = new ApiResponse<>(HttpStatus.OK.value(),"알림 목록 조회 성공", responseDto);

        return ResponseEntity.ok().body(response);

    }

    // 클라이언트가 서버에 연결될 때
    @GetMapping("/subscribe")
    public SseEmitter subscribe(@AuthUser Member member){
        SseEmitter sseEmitter = new SseEmitter();
        clients.put(member.getId(), sseEmitter);
        System.out.println( clients


        );

        // 연결 로그 출력
        System.out.println("Client connected: " + member.getId());

        // 연결이 닫히거나 타임아웃이 발생하면 클라이언트 목록에서 제거
        sseEmitter.onCompletion(() -> clients.remove(member.getId()));
        sseEmitter.onTimeout(() -> clients.remove(member.getId()));

        return sseEmitter;

    }

    @GetMapping("/send")
    public String sendNotification(@AuthUser Member member){
        SseEmitter sseEmitter = clients.get(member.getId());
        System.out.println( clients        );
        if (sseEmitter != null){
            try{
                sseEmitter.send(SseEmitter.event().name("notification").data("you have a new notice!"));
            }catch (IOException e){
                clients.remove(member.getId());
                return "Error - sending notice";
            }
            return "notice sent!";
        }
        return "no client connected";
    }

    @GetMapping
    public SseEmitter testNotice(@AuthUser Member member){
        SseEmitter sseEmitter = new SseEmitter();
        clients.put(member.getId(),sseEmitter);

        System.out.println("print logger");

        // 연결이 닫히면 클라이언트 목록에서 제거
        sseEmitter.onCompletion(() -> clients.remove(member.getId()));
        sseEmitter.onTimeout(() -> clients.remove(member.getId()));

        return sseEmitter;
    }



    public void sendNotification(Long userId, String message){
        SseEmitter emitter = clients.get(userId);

        if (emitter != null){
            try{
                emitter.send(SseEmitter.event().name("notification").data(message));
            }catch (Exception e){
                clients.remove(userId);
            }
        }
    }
}
