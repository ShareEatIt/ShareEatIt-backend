package com.carpBread.shareEatIt.domain.member.service;

import com.carpBread.shareEatIt.domain.member.dto.*;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.member.repository.MemberRepository;
import com.carpBread.shareEatIt.domain.participation.repository.GratitudeStickerRepository;
import com.carpBread.shareEatIt.domain.sharingPost.entity.PostCategory;
import com.carpBread.shareEatIt.domain.sharingPost.entity.SharingPost;
import com.carpBread.shareEatIt.domain.sharingPost.repository.SharingPostRepository;
import com.carpBread.shareEatIt.global.exception.AppException;
import com.carpBread.shareEatIt.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j @Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final GratitudeStickerRepository gratitudeStickerRepository;
    private final SharingPostRepository sharingPostRepository;

    /* 멤버의 스티커 현황 찾기 */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public MemberStickerResponseDto findStickers(Long memberId){
        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_MEMBER, "member profile - GET error", "/members/stickers"));

        List<Object[]> stickers = gratitudeStickerRepository.countByGratitudeTypeByGiver(findMember.getId());
        Map<String , Long > responseList=new HashMap<>();

        for (Object[] objects : stickers){
            String type = (String) objects[0];
            Long count=(Long) objects[1];

            responseList.put(type,count);
        }

        StickersResponseDto stickersDto = StickersResponseDto.builder()
                .smile1(responseList.get("SMILE1"))
                .smile2(responseList.get("SMILE2"))
                .smile3(responseList.get("SMILE3"))
                .smile4(responseList.get("SMILE4"))
                .smile5(responseList.get("SMILE5"))
                .build();

        return MemberStickerResponseDto.builder()
                .profileImg(findMember.getProfileImgUrl())
                .nickname(findMember.getNickname())
                .email(findMember.getEmail())
                .stickers(stickersDto)
                .isKeywordAvail(findMember.getIsKeywordAvail())
                .isNoticeAvail(findMember.getIsNoticeAvail())
                .build();

    }

    public MemberProfileResponseDto updateProfile(Long memberId, MemberProfileUpdateRequestDto updateRequestDto) {
        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_MEMBER,
                        "member profile update - PUT error", "/members"));

        findMember.changeMemberProfile(updateRequestDto);
        Member updatedMember = memberRepository.save(findMember);

        return MemberProfileResponseDto.builder()
                .email(updatedMember.getEmail())
                .nickname(updatedMember.getNickname())
                .profileImg(updatedMember.getProfileImgUrl())
                .location(LocationResponseDtoComponent.builder()
                        .addressSt(updatedMember.getAddressSt())
                        .addressDetail(updatedMember.getAddressDetail())
                        .build())
                .build();

    }

    public MemberStickerResponseDto updateAvail(MemberAvailRequestDto dto, Long memberId) {
        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_MEMBER,
                        "member profile update - PATCH error", "/members"));

        findMember.changeAvail(dto);

        Member savedMember = memberRepository.save(findMember);

        return findStickers(savedMember.getId());

    }

    public MemberWithdrawalResponseDto withdrawal(Long memberId) {
        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_MEMBER,
                        "member withdrawal - DELETE error", "/members"));

        // 이미지 url 삭제 로직 추가 예정


        // kakao 연결 끊기
        disconnectKakaoRegistration(findMember.getAccessId(),findMember.getAccessToken());

        MemberWithdrawalResponseDto responseDto = MemberWithdrawalResponseDto.builder()
                        .id(findMember.getId())
                        .nickname(findMember.getNickname())
                        .email(findMember.getEmail())
                        .build();

        memberRepository.deleteById(memberId);

        return responseDto;

    }

    private void disconnectKakaoRegistration(Long accessId, String accessToken){
        WebClient webClient = WebClient.builder()
                .baseUrl("https://kapi.kakao.com")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();

        webClient
                .post()
                .uri(uriBuilder -> uriBuilder.path("/v1/user/unlink").build())
                .header("Authorization","Bearer "+accessToken)
                .retrieve();
    }


    public MemberSharingStatusResponseDto findMemberSharingStatus(Member writer) {

        MemberSharingStatusResponseComponent statusDto = getSharingStatusResponseComponent(writer);

        MemberCompletedProfileResponseComponent writerDto = MemberCompletedProfileResponseComponent.builder()
                .id(writer.getId())
                .email(writer.getEmail())
                .imgUrl(writer.getProfileImgUrl())
                .nickname(writer.getNickname())
                .sharingTotal(sharingPostRepository.countByWriter(writer))
                .build();

        return MemberSharingStatusResponseDto.builder()
                .writer(writerDto)
                .status(statusDto)
                .build();


    }

    private MemberSharingStatusResponseComponent getSharingStatusResponseComponent(Member writer){
        Long BAKERY =0l;
        Long BEVERAGE=0l;
        Long CONVENIENCE_FOOD=0l;
        Long KOREAN=0l;
        Long JAPANESE=0l;
        Long CHINESE=0l;
        Long WESTERN=0l;
        Long SNACK=0l;
        Long GROCERIES=0l;
        Long ETC=0l;

        List<Object[]> objects = sharingPostRepository.countByCategoryForWriter(writer);
        for (Object[] obj : objects){
            PostCategory category = (PostCategory) obj[0];
            Long count = (Long) obj[1];

            switch (category){
                case BAKERY -> BAKERY=count;
                case BEVERAGE -> BEVERAGE = count;
                case CONVENIENCE_FOOD -> CONVENIENCE_FOOD = count;
                case KOREAN -> KOREAN = count;
                case JAPANESE -> JAPANESE = count;
                case CHINESE -> CHINESE = count;
                case WESTERN -> WESTERN = count;
                case SNACK -> SNACK = count;
                case GROCERIES -> GROCERIES = count;
                case ETC -> ETC = count;
            }


        }

        return MemberSharingStatusResponseComponent.builder()
                .BAKERY(BAKERY)
                .BEVERAGE(BEVERAGE)
                .CONVENIENCE_FOOD(CONVENIENCE_FOOD)
                .KOREAN(KOREAN)
                .JAPANESE(JAPANESE)
                .CHINESE(CHINESE)
                .WESTERN(WESTERN)
                .SNACK(SNACK)
                .GROCERIES(GROCERIES)
                .ETC(ETC)
                .build();

    }
}
