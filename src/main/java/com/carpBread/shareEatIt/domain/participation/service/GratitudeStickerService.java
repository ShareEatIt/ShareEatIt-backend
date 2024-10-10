package com.carpBread.shareEatIt.domain.participation.service;

import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.member.repository.MemberRepository;
import com.carpBread.shareEatIt.domain.participation.dto.GratitudeResponseDto;
import com.carpBread.shareEatIt.domain.participation.entity.GratitudeSticker;
import com.carpBread.shareEatIt.domain.participation.entity.GratitudeType;
import com.carpBread.shareEatIt.domain.participation.entity.Participation;
import com.carpBread.shareEatIt.domain.participation.repository.GratitudeStickerRepository;
import com.carpBread.shareEatIt.domain.participation.repository.ParticipationRepository;
import com.carpBread.shareEatIt.domain.sharingPost.entity.PostStatus;
import com.carpBread.shareEatIt.domain.sharingPost.entity.SharingPost;
import com.carpBread.shareEatIt.domain.sharingPost.repository.SharingPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class GratitudeStickerService {

//    private final MemberRepository memberRepository;
    private final SharingPostRepository sharingPostRepository;
    private final ParticipationRepository participationRepository;
    private final GratitudeStickerRepository gratitudeStickerRepository;

    /* 고마움 스티커 생성 */
    public GratitudeResponseDto createGratitudeSticker(Long postId, Member member, GratitudeType gratitudeType){

        // 파라미터 값이 누락된 경우 예외 처리
        if (postId == null || member.getId() == null) {
            throw new IllegalArgumentException("postId or memberId cannot be null");
        }
//
//        // member 객체 찾기
//        Member member = memberRepository.findById(memberId)
//                .orElseThrow(() -> new RuntimeException("해당 Id의 member를 찾을 수 없습니다."));

        // SharingPost 객체 찾기
        SharingPost post = sharingPostRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("해당 Id의 post를 찾을 수 없습니다."));

        // 검증1 : 해당 나눔글의 상태가 COMPLETED인지 확인
        if (!post.getStatus().equals(PostStatus.COMPLETED)){
            throw new IllegalStateException("완료되지 않은 나눔입니다.");
        }

        // 해당 나눔글을 참조하고 있는 '참여' 데이터 중 상태가 COMPLETED인 참여 찾아오기
        Participation participation = findSingleParticipation(postId);

        // 검증4 : 찾은 '참여' 데이터의 receiverId가 memberId와 같은지 확인
        if (!member.getId().equals(participation.getReceiver().getId())){
            throw new IllegalStateException("해당 나눔을 받은 멤버가 아닙니다.");
        }

        // 검증 5 : 이미 고마움을 남긴 경우
        if (gratitudeStickerRepository.existsByParticipationId(participation.getId())){
            throw  new IllegalStateException("이미 고마움을 남긴 나눔입니다.");
        }

        // GratitudeSticker 객체 생성
        GratitudeSticker gratitudeSticker = GratitudeSticker.builder()
                .post(post)
                .participation(participation)
                .giver(post.getWriter())
                .reviewer(member)
                .gratitudeType(gratitudeType)
                .build();
        // 저장
        GratitudeSticker savedGratitude = gratitudeStickerRepository.save(gratitudeSticker);

        // 응답 dto로 변환후 반환
        return GratitudeResponseDto.from(savedGratitude);

    }

    // 리스트에 하나의 객체만 있는지 확인한 후, 그 객체를 반환하는 함수
    public Participation findSingleParticipation(Long postId) {
        List<Participation> participationList = participationRepository.findByPostIdAndStatus(postId);

        if (participationList.isEmpty()) { // 검증2 : 찾지 못한 경우
            throw new RuntimeException("해당 나눔글의 완료된 참여정보를 불러올 수 없습니다.");
        }

        if (participationList.size() > 1) { //검증3 : COMPLETED인 참여가 여러개인 경우
            throw new IllegalStateException("같은 나눔글에 대해 참여 상태가 COMPLETED인 참여 객체가 여러개입니다.");
        }

        // 리스트에 하나의 요소만 있을 때 그 객체를 반환
        return participationList.get(0);
    }


    /* 고마움 스티커 수정 */
    public GratitudeResponseDto updateGratitudeStickers(Long gratitudeStickerId, Member member, GratitudeType gratitudeType) {

        // 파라미터 값이 누락된 경우 예외 처리
        if (gratitudeStickerId == null || member == null) {
            throw new IllegalArgumentException("postId or memberId cannot be null");
        }

        // gratitudeSticker 객체 찾기
        GratitudeSticker gratitudeSticker = gratitudeStickerRepository.findById(gratitudeStickerId)
                .orElseThrow(() -> new RuntimeException("해당 Id의 gratitudeSticker를 찾을 수 없습니다."));

        // 검증1 : 찾은 gratitudeSticker의 reviewerId가 memberId와 같은지 확인
        Member reviewer = gratitudeSticker.getReviewer();
        if (reviewer == null || !member.getId().equals(reviewer.getId())){
            throw new IllegalStateException("해당 나눔을 받은 멤버가 아닙니다.");
        }

        // 업데이트
        gratitudeSticker.updateGratitude(gratitudeType);
        // 저장
        GratitudeSticker savedGratitude = gratitudeStickerRepository.save(gratitudeSticker);

        // 응답 dto로 변환후 반환
        return GratitudeResponseDto.from(savedGratitude);

    }
}
