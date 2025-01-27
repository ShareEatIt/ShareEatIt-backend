package com.carpBread.shareEatIt.domain.notice.service;

import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.notice.dto.NoticeListResponseDto;
import com.carpBread.shareEatIt.domain.notice.dto.NoticeResponseComponent;
import com.carpBread.shareEatIt.domain.notice.dto.NoticeResponseDto;
import com.carpBread.shareEatIt.domain.notice.entity.Notice;
import com.carpBread.shareEatIt.domain.notice.repository.NoticeRepository;
import com.carpBread.shareEatIt.global.exception.CustomException;
import com.carpBread.shareEatIt.global.exception.CustomExceptionStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/*notice 관련 db 리스트 조회 기능을 담당하는 service*/
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

        Boolean isRead=false;
        if (componentList.size()==0)
            isRead=true;


        return NoticeListResponseDto.builder()
                .isRead(isRead)
                .noticeList(componentList)
                .build();

    }
    public NoticeResponseDto findNoticeById(Member member, Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> null /*new CustomException(CustomExceptionStatus.NOT_FOUND_NOTICE, "ID=" + id + "에 해당하는 알림을 찾을 수 없습니다","/notice/"+id)*/);

        if (notice.getMember().getId() != member.getId())
//            throw new CustomException(CustomExceptionStatus.UNAUTHORIZED_USER,"해당 알람을 확인할 수 없는 사용자입니다","/notice/"+id);
        if (notice.getIsRead())
//            throw new CustomException(CustomExceptionStatus.ALREADY_READ,"이미 읽은 알림입니다","/notice/"+id);

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


}
