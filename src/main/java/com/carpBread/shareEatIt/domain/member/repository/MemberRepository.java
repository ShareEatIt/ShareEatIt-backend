package com.carpBread.shareEatIt.domain.member.repository;

import com.carpBread.shareEatIt.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member , Long> {
}
