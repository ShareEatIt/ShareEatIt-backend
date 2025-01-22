package com.carpBread.shareEatIt.domain.member.service;

import com.carpBread.shareEatIt.domain.member.dto.request.SignUpRequestDto;
import com.carpBread.shareEatIt.domain.member.dto.response.SignUpResponseDto;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.member.entity.Provider;
import com.carpBread.shareEatIt.domain.member.repository.MemberRepository;
import com.carpBread.shareEatIt.global.exception.CustomException;
import com.carpBread.shareEatIt.global.exception.CustomExceptionStatus;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


/* 사용자 회원가입 service */
@Service
@RequiredArgsConstructor
public class SignUpService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final GeometryFactory geometryFactory;

    /* 회원가입 */
    public SignUpResponseDto registerNewMember(SignUpRequestDto dto) {

        // username 고유 여부 인증
        boolean isUsernameOccupied = memberRepository.existsByUsername(dto.getUsername());
        if (isUsernameOccupied){
            throw new CustomException(CustomExceptionStatus.ALREADY_EXISTS_USERNAME,dto.getUsername()+"은 이미 존재하는 username 입니다.", "/signup");
        }

        // email 있다면 고유 여부 인증
        if (dto.getEmail()!=null){
            boolean isEmailOccupied = memberRepository.existsByEmail(dto.getEmail());
            if (isEmailOccupied){
                throw new CustomException(CustomExceptionStatus.ALREADY_EXISTS_EMAIL,dto.getEmail()+"은 이미 존재하는 email 입니다.", "/signup");
            }
        }

        // 비밀번호 해시 암호화
        String encodedPW = encodingPassword(dto.getPassword());

        // 사용자 위치 point 객체 생성
        Point locationPoint = createLocationPoint(dto.getLatitude(), dto.getLongitude());

        // new Member 객체 생성
        Member newMember = createNewMember(dto,locationPoint,encodedPW);

        Member savedMember = memberRepository.save(newMember);

        return SignUpResponseDto.builder()
                .id(savedMember.getId())
                .username(savedMember.getUsername())
                .build();

    }

    /* 비밀번호 해시 암호화 */
    private String encodingPassword(String password){
        return passwordEncoder.encode(password);
    }

    /* 사용자 위치 point 객체 생성 */
    private Point createLocationPoint(Double latitude, Double longitude){
        return geometryFactory.createPoint(new Coordinate(longitude,latitude));
    }

    /* 새로운 사용자 생성 */
    private Member createNewMember(SignUpRequestDto dto, Point locationPoint, String encodedPW){
        return Member.builder()
                .email(dto.getEmail()).nickname(dto.getNickname())
                .username(dto.getUsername()).password(encodedPW)
                .isKeywordAvail(dto.getIsKeywordAvail()).isNoticeAvail(dto.getIsNoticeAvail())
                .addressSt(dto.getAddressSt()).addressDetail(dto.getAddressDetail())
                .locationPoint(locationPoint)
                .provider(Provider.toEnum(dto.getProvider()))
                .build();
    }

}
