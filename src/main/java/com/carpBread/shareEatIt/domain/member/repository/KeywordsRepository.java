package com.carpBread.shareEatIt.domain.member.repository;

import com.carpBread.shareEatIt.domain.member.entity.Keywords;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeywordsRepository extends JpaRepository<Keywords, Long> {
}
