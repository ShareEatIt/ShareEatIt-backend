package com.carpBread.shareEatIt.domain.member.service;

import com.carpBread.shareEatIt.domain.member.dto.MemberCreationDto;
import com.carpBread.shareEatIt.domain.member.dto.request.SignUpRequestDto;
import com.carpBread.shareEatIt.domain.member.dto.response.SignUpResponseDto;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.member.entity.Provider;
import com.carpBread.shareEatIt.domain.member.repository.MemberRepository;
import com.carpBread.shareEatIt.domain.member.service.module.MemberModuleService;
import com.carpBread.shareEatIt.global.exception.CustomException;
import com.carpBread.shareEatIt.global.exception.CustomExceptionStatus;
import com.carpBread.shareEatIt.global.exception.Domain;
import com.carpBread.shareEatIt.global.service.module.S3ImageUploadService;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/* 사용자 회원가입 component service */
@Service @Transactional
@RequiredArgsConstructor
public class SignUpService {
    private final MemberModuleService memberModuleService;
    private final S3ImageUploadService s3ImageUploadService;
    private final PasswordEncoder passwordEncoder;
    private final GeometryFactory geometryFactory;

    /* 회원가입 */
    public SignUpResponseDto registerNewMember(MultipartFile profileImg, SignUpRequestDto dto) {

        // username 고유 여부 인증
        memberModuleService.alreadyExistedUsername(dto.getUsername());

        // email 있다면 고유 여부 인증
        if (dto.getEmail()!=null){
            memberModuleService.alreadyExistedEmail(dto.getEmail());
        }

        // profileImg 저장
        String profileImgUrl=null;
        if (!profileImg.isEmpty()){
            profileImgUrl= s3ImageUploadService.uploadOneImageToS3Bucket(profileImg);

        }

        // 비밀번호 해시 암호화
        String encodedPW = encodingPassword(dto.getPassword());

        // 사용자 위치 point 객체 생성
        Point locationPoint = createLocationPoint(dto.getLatitude(), dto.getLongitude());

        // DB에 저장 후 response 객체 반환
        return memberModuleService.signUp(new MemberCreationDto(dto, locationPoint, encodedPW, profileImgUrl));

    }

    /* 비밀번호 해시 암호화 */
    private String encodingPassword(String password){
        return passwordEncoder.encode(password);
    }

    /* 사용자 위치 point 객체 생성 */
    private Point createLocationPoint(Double latitude, Double longitude){
        return geometryFactory.createPoint(new Coordinate(longitude,latitude));
    }

}
