package com.carpBread.shareEatIt.domain.member.repository;

import com.carpBread.shareEatIt.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member , Long> {

    Optional<Member> findByEmail(String email);

    // 해당 email이 존재하는지 여부
    boolean existsByEmail(String email);

    // 해당 username이 존재하는지 여부
    boolean existsByUsername(String username);

    Optional<Member> findByUsername(String username);

}
