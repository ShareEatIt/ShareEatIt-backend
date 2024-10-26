package com.carpBread.shareEatIt.domain.sharingPost.repository;

import com.carpBread.shareEatIt.domain.sharingPost.entity.PostImgUrl;
import com.carpBread.shareEatIt.domain.sharingPost.entity.SharingPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostImgUrlRepository extends JpaRepository<PostImgUrl, Long> {

    Optional<PostImgUrl> findByImgOrderAndPost(int imgOrder, SharingPost post);

    void deleteAllByPost(SharingPost post);

    List<PostImgUrl> findByPost(SharingPost post);

    void deleteByPostAndImgOrder(SharingPost post, int imgOrder);
}
