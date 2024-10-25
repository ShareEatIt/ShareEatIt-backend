package com.carpBread.shareEatIt.domain.sharingPost.service;

import com.carpBread.shareEatIt.domain.sharingPost.dto.SharingPostCreateRequestDto;
import com.carpBread.shareEatIt.domain.sharingPost.dto.SharingPostCreateResponseDto;
import com.carpBread.shareEatIt.domain.sharingPost.repository.SharingPostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service @Transactional
@RequiredArgsConstructor
public class SharingPostService {
    private final SharingPostRepository sharingPostRepository;

    public SharingPostCreateResponseDto createSharingPost(List<MultipartFile> imgList, SharingPostCreateRequestDto dto) {



    }

    private List<String> uploadPostImgToS3Bucket(List<MultipartFile> imgList){


    }

}
