package com.carpBread.shareEatIt.domain.notice.repository;

import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.notice.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    List<Notice> findByMemberAndIsRead(Member member, Boolean isRead);

    List<Notice> findByMember(Member member);

}


