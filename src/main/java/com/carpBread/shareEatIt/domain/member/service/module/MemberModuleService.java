package com.carpBread.shareEatIt.domain.member.service.module;

import com.carpBread.shareEatIt.domain.member.dto.MemberCreationDto;
import com.carpBread.shareEatIt.domain.member.dto.request.SignUpRequestDto;
import com.carpBread.shareEatIt.domain.member.dto.response.SignUpResponseDto;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.member.repository.MemberRepository;
import com.carpBread.shareEatIt.global.exception.CustomException;
import com.carpBread.shareEatIt.global.exception.CustomExceptionStatus;
import com.carpBread.shareEatIt.global.exception.Domain;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/* MemberRepository를 사용하는 단순 Member Service들을 담당하는 Member Module Service */
@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class MemberModuleService {

    private final MemberRepository memberRepository;

    // 회원가입 메소드
    public SignUpResponseDto signUp(MemberCreationDto dto){

        SignUpRequestDto requestDto = dto.getRequestDto();

        // Member 객체 생성
        Member newMember = new Member(requestDto.getEmail(), requestDto.getUsername(), dto.getEncodedPW(),
                requestDto.getNickname(), requestDto.getIsNoticeAvail(), requestDto.getIsKeywordAvail(),
                requestDto.getAddressSt(), requestDto.getAddressDetail(), dto.getLocationPoint(),
                requestDto.getProvider(), dto.getProfileImg());

        Member saved = memberRepository.save(newMember);

        return new SignUpResponseDto(
                saved.getId(), saved.getUsername()
        );

    }

    // username 고유 여부 반환
    public void alreadyExistedUsername(String username){
        if (memberRepository.existsByUsername(username)){
            throw new CustomException(
                    CustomExceptionStatus.ALREADY_EXISTS_USERNAME,
                    "이미 존재하는 USERNAME입니다.",
                    this.getClass().getSimpleName(),
                    username,
                    Domain.MEMBER
            );
        }
    }

    // refreshToken 갱신
    public void updateRefreshToken(String email, String refreshToken){
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(
                                CustomExceptionStatus.NOT_FOUND_MEMBER,
                                "EMAIL로 찾을 수 없는 사용자입니다. 가입되어있지 않습니다.",
                                this.getClass().getSimpleName(),
                                email,
                                Domain.MEMBER
                        )
                );

        member.updateRefreshToken(refreshToken);
        memberRepository.save(member);

    }

    // email 고유 여부 인증
    public void alreadyExistedEmail(String email){
        if (memberRepository.existsByEmail(email)){
            throw new CustomException(
                    CustomExceptionStatus.ALREADY_EXISTS_EMAIL,
                    "이미 존재하는 EMAIL 입니다",
                    this.getClass().getSimpleName(),
                    email,
                    Domain.MEMBER
            );
        }
    }
}
