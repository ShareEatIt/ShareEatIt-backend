package com.carpBread.shareEatIt.domain.participation.service;

import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.member.repository.MemberRepository;
import com.carpBread.shareEatIt.domain.participation.dto.ParticipationRequestDto;
import com.carpBread.shareEatIt.domain.participation.dto.ParticipationResponseDto;
import com.carpBread.shareEatIt.domain.participation.entity.Participation;
import com.carpBread.shareEatIt.domain.participation.entity.ParticipationStatus;
import com.carpBread.shareEatIt.domain.participation.repository.ParticipationRepository;
import com.carpBread.shareEatIt.domain.sharingPost.entity.SharingPost;
import com.carpBread.shareEatIt.domain.sharingPost.repository.SharingPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ParticipationService {

    private final ParticipationRepository participationRepository;
    private final SharingPostRepository sharingPostRepository;
    private final MemberRepository memberRepository;

    /* 참여 생성 - 나눔글 채팅 참여 */
    public ParticipationResponseDto createParticipation(Long receiverId, ParticipationRequestDto requestDto) {

        // member 조회 - 로그인 구현 이후 수정 필요
        Member receiver = memberRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("해당ID의 회원을 찾지 못했습니다."));

        // requestDto로 받아온 postId의 나눔글 조회
        SharingPost post = sharingPostRepository.findById(requestDto.getSharingPostId())
                .orElseThrow(() -> new RuntimeException("해당ID의 나눔글을 찾지 못했습니다."));  // 나중에 custom error로 바꾸기

        // Participation 객체 생성
        Participation participation = Participation.builder()
                .post(post)
                .status(ParticipationStatus.CHATTING)
                .giver(post.getWriter())
                .receiver(receiver)
                .isGiverInChat(true)
                .isReceiverInChat(true)
                .build();

        // Participation 객체 저장
        Participation savedParticipation = participationRepository.save(participation);

        // 응답 DTO 생성
        ParticipationResponseDto responseDto = ParticipationResponseDto.from(savedParticipation);
        return responseDto;
    }








}
