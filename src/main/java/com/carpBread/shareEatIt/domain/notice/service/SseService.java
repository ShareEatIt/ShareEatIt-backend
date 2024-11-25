package com.carpBread.shareEatIt.domain.notice.service;

/* sseEmitter에 등록하고 연결을 삭제하는 서비스*/

import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.notice.controller.NoticeController;
import com.carpBread.shareEatIt.domain.notice.dto.NoticeCreateDto;
import com.carpBread.shareEatIt.global.exception.AppException;
import com.carpBread.shareEatIt.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/* sseEmitter 객체를 관리하고 sseEmitter 객체로 알림을 보내는 서비스 */
@Service @Slf4j
@RequiredArgsConstructor
public class SseService {

    // SseEmitter 객체
    private final Map<Long, SseEmitter> clients = new ConcurrentHashMap<>();
    private final RedisTemplate<String, Object> redisTemplate;

    // client 연결
    public SseEmitter registerClient(Long userId){
        SseEmitter emitter = new SseEmitter(0L);
        clients.put(userId, emitter);

        // 연결 종료 처리
        emitter.onCompletion(() -> clients.remove(userId));
        emitter.onTimeout(()->clients.remove(userId));
        emitter.onError((e)->clients.remove(userId));

        log.debug("알람 객체에 등록됨"+userId);

        // redis에 사용자 등록 정보 저장
        redisTemplate.opsForHash().put("sse:clients", userId.toString(), emitter);
        return emitter;

    }

    // client 삭제
    public void unregisterClient(Long userId){
        clients.remove(userId);
        redisTemplate.opsForHash().delete("sse:clients",userId.toString());
    }

    // 알림 보내기
    public void sendNotification(Long memberId, NoticeCreateDto dto){
        SseEmitter emitter = clients.get(memberId);

        if (emitter != null){
            try{
                log.debug("알림 로그"+System.nanoTime()+dto.getMessage()+'['+dto.getTitle()+']');
                emitter.send(SseEmitter.event().name("notice:"+dto.getNoticeType()+":"+dto.getId()).data(dto));
            }catch (IOException e){
                clients.remove(memberId);
                redisTemplate.opsForHash().delete("sse:clients", memberId.toString());
                throw new AppException(ErrorCode.NOTICE_SEND_FAIL,"알림을 전송하는 과정에서 오류가 발생했습니다","[INNER LOGIC FAIL _ NO URL]");
            }
        }
    }


    // 알람 목록 clients에 등록되어 있는 사용자인지 확인
    public Boolean isRegistered(Long userId){
        return clients.containsKey(userId);
    }

}
