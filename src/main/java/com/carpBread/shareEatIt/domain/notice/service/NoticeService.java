package com.carpBread.shareEatIt.domain.notice.service;

import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.notice.controller.NoticeController;
import com.carpBread.shareEatIt.domain.notice.dto.NoticeCreateDto;
import com.carpBread.shareEatIt.domain.notice.dto.NoticeListResponseDto;
import com.carpBread.shareEatIt.domain.notice.dto.NoticeResponseComponent;
import com.carpBread.shareEatIt.domain.notice.dto.NoticeResponseDto;
import com.carpBread.shareEatIt.domain.notice.entity.Notice;
import com.carpBread.shareEatIt.domain.notice.repository.NoticeRepository;
import com.carpBread.shareEatIt.global.exception.AppException;
import com.carpBread.shareEatIt.global.exception.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service @Transactional(value = Transactional.TxType.REQUIRES_NEW)
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    public NoticeListResponseDto findUnreadNoticeList(Member member) {
        List<Notice> unReadNoticeList = noticeRepository.findByMemberAndIsRead(member, false);

        List<NoticeResponseComponent> componentList = new ArrayList<>();
        for (Notice notice : unReadNoticeList){
            NoticeResponseComponent component = NoticeResponseComponent.builder()
                    .id(notice.getId())
                    .title(notice.getTitle())
                    .message(notice.getMessage())
                    .build();
            componentList.add(component);
            notice.changeIsRead(true);
            noticeRepository.save(notice);
        }

        return NoticeListResponseDto.builder()
                .isRead(false)
                .noticeList(componentList)
                .build();

    }
    public NoticeResponseDto findNoticeById(Member member, Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_NOTICE, "ID=" + id + "에 해당하는 알림을 찾을 수 없습니다","/notice/"+id));

        if (notice.getMember().getId() != member.getId())
            throw new AppException(ErrorCode.UNAUTHORIZED_USER,"해당 알람을 확인할 수 없는 사용자입니다","/notice/"+id);
        if (notice.getIsRead())
            throw new AppException(ErrorCode.ALREADY_READ,"이미 읽은 알림입니다","/notice/"+id);

        notice.changeIsRead(true);
        noticeRepository.save(notice);
        return NoticeResponseDto.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .message(notice.getMessage())
                .noticeType(notice.getType().name())
                .createdAt(notice.getCreatedAt())
                .build();

    }


    public static void sendNotification(Member member, NoticeCreateDto dto){
        SseEmitter emitter = NoticeController.getSseEmitterByMemberId(member.getId());

        if (emitter != null){
            try{
                emitter.send(SseEmitter.event().name("notice:"+dto.getNoticeType()+":"+dto.getId()).data(dto));
            }catch (IOException e){
                NoticeController.removeMemberFromClients(member.getId());
                throw new AppException(ErrorCode.NOTICE_SEND_FAIL,"알림을 전송하는 과정에서 오류가 발생했습니다","[INNER LOGIC FAIL _ NO URL]");
            }
        }
    }



}
