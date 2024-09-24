package com.carpBread.shareEatIt.domain.sharingPost.repository;

import com.carpBread.shareEatIt.domain.sharingPost.entity.SharingPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SharingPostRepository extends JpaRepository<SharingPost, Long> {
}
