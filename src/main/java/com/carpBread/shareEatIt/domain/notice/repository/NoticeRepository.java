package com.carpBread.shareEatIt.domain.notice.repository;

import com.carpBread.shareEatIt.domain.notice.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
}
