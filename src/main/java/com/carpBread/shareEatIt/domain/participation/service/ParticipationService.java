package com.carpBread.shareEatIt.domain.participation.service;

import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.member.repository.MemberRepository;
import com.carpBread.shareEatIt.domain.participation.dto.*;
import com.carpBread.shareEatIt.domain.participation.entity.Participation;
import com.carpBread.shareEatIt.domain.participation.entity.ParticipationStatus;
import com.carpBread.shareEatIt.domain.participation.repository.ParticipationRepository;
import com.carpBread.shareEatIt.domain.sharingPost.entity.PostStatus;
import com.carpBread.shareEatIt.domain.sharingPost.entity.PostType;
import com.carpBread.shareEatIt.domain.sharingPost.entity.SharingPost;
import com.carpBread.shareEatIt.domain.sharingPost.repository.SharingPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ParticipationService {

    private final ParticipationRepository participationRepository;
    private final SharingPostRepository sharingPostRepository;

    /* 참여 생성 - 나눔글 채팅 참여 */
    public ParticipationResponseDto createParticipation(Member receiver, ParticipationRequestDto requestDto) {

        // requestDto로 받아온 postId의 나눔글 조회
        SharingPost post = sharingPostRepository.findById(requestDto.getSharingPostId())
                .orElseThrow(() -> new RuntimeException("해당ID의 나눔글을 찾지 못했습니다."));  // 나중에 custom error로 바꾸기

        // Participation 객체 생성
        Participation participation = Participation.builder()
                .post(post)
                .status(ParticipationStatus.AVAILABLE)
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


    /* 사용자가 나눔받은 모든 기록 조회 */
    public ParticipationHistoryListResponseDto findAllParticipation(Member receiver) {

        // 사용자 = receiver이고 나눔완료 상태인 모든 '참여'의 나눔글 조회
        List<SharingPost> participatedPosts = participationRepository.findSharingPostByUserAndStatus(receiver.getId());

        // 조회된 각 나눔글을 응답dto 리스트로 변환
        List<ParticipationHistoryResponseDto> dtoList = convertDtoToList(participatedPosts);
        return new ParticipationHistoryListResponseDto(dtoList);

    }


    /* 사용자가 특정 provider의 나눔을 받은 모든 기록 조회 */
    public ParticipationHistoryListResponseDto findAllParticipationByProvider(Member receiver, PostType provider) {

        // 사용자 = receiver이고 상태 = 나눔완료인 모든 '참여'의 나눔글 중 특정 provider의 글 조회
        List<SharingPost> participatedPosts = participationRepository.findSharingPostByUserAndStatusAndPostType(receiver.getId(), provider);

        // 조회된 각 나눔글을 응답dto 리스트로 변환
        List<ParticipationHistoryResponseDto> dtoList = convertDtoToList(participatedPosts);
        return new ParticipationHistoryListResponseDto(dtoList);

    }

    // list를 dto로 변환
    private List<ParticipationHistoryResponseDto> convertDtoToList(List<SharingPost> participatedPosts){
        return participatedPosts.stream()
                .map(ParticipationHistoryResponseDto::from)
                .collect(Collectors.toList());
    }


    /* 참여 상태 변경 */
    public ParticipationUpdateStatusResponseDto updateStatus(Long ptId, Member receiver, ParticipationStatus ptStatus) {

        // participation 객체 찾아오기
        Participation participation = participationRepository.findById(ptId)
                .orElseThrow(() -> new RuntimeException("해당 ID의 참여기록을 찾지 못했습니다."));

        String sharingPostStatus = participation.getPost().getStatus().toString();
        String participationStatus = ptStatus.toString();

        // 검증1: 해당 참여의 나눔글이 이미 바꾸려는 상태인지 확인 (이미 찜 or 나눔완료 된 나눔글의 참여인 경우)
        if (sharingPostStatus.equals(participationStatus)){
            throw new RuntimeException("현재 나눔글의 상태와 동일한 상태로 변경할 수 없습니다.");
        }

        // 검증2: 사용자가 나눔자의 writer인지 확인
        if (receiver.getId() != participation.getPost().getWriter().getId()){
            throw new RuntimeException("나눔글 작성자가 아니므로 나눔 상태를 변경할 수 없습니다.");
        }

        // 상태 변경
        participation.updateStatus(ptStatus);

        // 동시에 post의 status도 변경 (클래스에 붙은 Transactional로 원자성 보장)
        try {
            PostStatus postStatus = PostStatus.valueOf(participationStatus);
            System.out.println("변환된 PostStatus: " + postStatus);
            SharingPost post = participation.getPost();
            post.updateStatus(postStatus);
        } catch (IllegalArgumentException e) {
            System.out.println("잘못된 상태값입니다: " + participationStatus);
        }

        // 변경한 내용 저장
        participationRepository.save(participation);

        // 응답 DTO 생성
        ParticipationUpdateStatusResponseDto responseDto = ParticipationUpdateStatusResponseDto.from(participation);
        return responseDto;

    }
}
