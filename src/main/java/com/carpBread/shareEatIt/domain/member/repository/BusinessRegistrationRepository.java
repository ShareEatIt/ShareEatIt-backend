package com.carpBread.shareEatIt.domain.member.repository;

import com.carpBread.shareEatIt.domain.member.entity.BusinessRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessRegistrationRepository extends JpaRepository<BusinessRegistration, Long> {
}
