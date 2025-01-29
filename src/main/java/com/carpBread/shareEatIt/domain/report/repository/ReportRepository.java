package com.carpBread.shareEatIt.domain.report.repository;

import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.report.entity.Report;
import com.carpBread.shareEatIt.domain.sharingPost.entity.SharingPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report , Long> {

    boolean existsByReporterAndPost(Member reporter, SharingPost post);
}
