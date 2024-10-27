package com.carpBread.shareEatIt.domain.notice.service;

import com.carpBread.shareEatIt.domain.notice.controller.NoticeController;
import com.carpBread.shareEatIt.domain.sharingPost.entity.PostStatus;
import com.carpBread.shareEatIt.domain.sharingPost.entity.SharingPost;
import com.carpBread.shareEatIt.domain.sharingPost.repository.SharingPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeCreateService {

    private final SharingPostRepository sharingPostRepository;
    private final NoticeController noticeController;

    @Scheduled(fixedRate = 7200000)
    public void checkAndNoticeUsersTest(){
        List<SharingPost> postList=sharingPostRepository.findAllByNoticedFalseAndStatus(PostStatus.COMPLETED);
        for(SharingPost post : postList){
            Long writerId = post.getWriter().getId();
            noticeController.sendNotification(writerId, "매칭된 게시물이 있습니다");
            post.changeNoticed(true);
            sharingPostRepository.save(post);
        }
    }
}
