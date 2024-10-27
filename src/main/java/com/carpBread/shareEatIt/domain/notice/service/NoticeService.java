package com.carpBread.shareEatIt.domain.notice.service;

import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.notice.dto.NoticeListResponseDto;
import com.carpBread.shareEatIt.domain.notice.dto.NoticeResponseComponent;
import com.carpBread.shareEatIt.domain.notice.entity.Notice;
import com.carpBread.shareEatIt.domain.notice.repository.NoticeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service @Transactional
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

}
