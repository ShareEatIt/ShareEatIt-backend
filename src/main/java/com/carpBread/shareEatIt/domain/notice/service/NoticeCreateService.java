package com.carpBread.shareEatIt.domain.notice.service;

import com.carpBread.shareEatIt.domain.notice.controller.NoticeController;
import com.carpBread.shareEatIt.domain.sharingPost.entity.PostStatus;
import com.carpBread.shareEatIt.domain.sharingPost.entity.SharingPost;
import com.carpBread.shareEatIt.domain.sharingPost.repository.SharingPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class NoticeCreateService {

    private final SharingPostRepository sharingPostRepository;
    private final NoticeController noticeController;

    private final Map<Long, SseEmitter> clients = new ConcurrentHashMap<>();

    public SseEmitter subscribe(Long memberId){
        SseEmitter sseEmitter = new SseEmitter();
        clients.put(memberId, sseEmitter);

        // 연결 종료 또는 타임아웃 시 클라이언트 제거
        sseEmitter.onCompletion(() -> clients.remove(memberId));
        sseEmitter.onTimeout(() -> clients.remove(memberId));

        return sseEmitter;
    }

    public void sendNotification(Long memberId, String message){
        SseEmitter sseEmitter = clients.get(memberId);

        System.out.println("NoticeCreateService.sendNotification");
        if (sseEmitter != null) {
            try {
                sseEmitter.send(SseEmitter.event().name("notification").data(message));
            } catch (IOException e) {
                clients.remove(memberId);  // 전송 오류 시 클라이언트 제거
            }
        }
    }


}
