package com.carpBread.shareEatIt.domain.report.repository;

import com.carpBread.shareEatIt.domain.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report , Long> {
}
