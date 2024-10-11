package com.carpBread.shareEatIt.domain.participation.service;

import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.participation.dto.*;
import com.carpBread.shareEatIt.domain.participation.entity.Participation;
import com.carpBread.shareEatIt.domain.participation.entity.ParticipationStatus;
import com.carpBread.shareEatIt.domain.participation.repository.ParticipationRepository;
import com.carpBread.shareEatIt.domain.sharingPost.entity.PostStatus;
import com.carpBread.shareEatIt.domain.sharingPost.entity.PostType;
import com.carpBread.shareEatIt.domain.sharingPost.entity.SharingPost;
import com.carpBread.shareEatIt.domain.sharingPost.repository.SharingPostRepository;
import com.carpBread.shareEatIt.global.exception.AppException;
import com.carpBread.shareEatIt.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ParticipationService {

    private final ParticipationRepository participationRepository;
    private final SharingPostRepository sharingPostRepository;

    /* 참여 생성 - 나눔글 채팅 참여 */
    public ParticipationResponseDto createParticipation(Member receiver, ParticipationRequestDto requestDto) {

        // requestDto로 받아온 postId의 나눔글 조회
        SharingPost post = sharingPostRepository.findById(requestDto.getSharingPostId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_SHARINGPOST, "해당ID의 나눔글을 찾지 못했습니다.", "/participations"));


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
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_PARTICIPATION, "해당 ID의 참여기록을 찾지 못했습니다.", "/participation/"+ptId));

        String sharingPostStatus = participation.getPost().getStatus().toString();
        String participationStatus = ptStatus.toString();

        // 검증1: 해당 참여의 나눔글과 동일한 상태로 변경하려는 상태인지 확인 (이미 찜 or 나눔완료 된 나눔글의 참여인 경우)
        if (sharingPostStatus.equals(participationStatus)){
            log.error("이미 나눔글이 {}인 상태로, 같은 상태로 변경 불가", sharingPostStatus);
            if (sharingPostStatus.equals("COMPLETED")){
                throw new AppException(ErrorCode.ALREADY_COMPLETED_SHARINGPOST, "이미 나눔 완료된 나눔입니다.", "/participation/"+ptId);
            }
            else if (sharingPostStatus.equals("MATCHED")){
                throw new AppException(ErrorCode.ALREADY_MATCHED_SHARINGPOST, "이미 찜 상태인 나눔입니다.", "/participation/"+ptId);
            }
            else {
                throw new AppException(ErrorCode.ALREADY_AVAILABLE_SHARINGPSOT, "현재 나눔 가능한 상태로 변경할 상태가 없습니다.", "/participation/"+ptId);
            }
        }

        // 검증2: 사용자가 나눔자의 writer인지 확인
        if (!receiver.getId().equals(participation.getPost().getWriter().getId())){
            log.error("사용자 != 나눔글 작성자");
            throw new AppException(ErrorCode.NOT_WRITER_OF_SHARINGPOST, "나눔글 작성자가 아니므로 나눔 상태를 변경할 수 없습니다.", "/participation/"+ptId);
        }

        // 상태 변경
        participation.updateStatus(ptStatus);

        // 동시에 post의 status도 변경 (클래스에 붙은 Transactional로 원자성 보장)
        try {
            PostStatus postStatus = PostStatus.valueOf(participationStatus);
            log.info("변환된 SharingPostd의 상태 : {}", postStatus.toString());
            SharingPost post = participation.getPost();
            post.updateStatus(postStatus);
        } catch (IllegalArgumentException e) {
            log.error("잘못된 상태값으로, 해당 나눔글의 상태 변경에 실패");
            throw new AppException(ErrorCode.INVALID_STATUS_VALUE ,"잘못된 상태값으로, 해당 나눔글의 상태 변경에 실패하였습니다.", "/participation/"+ptId);
        }

        // 변경한 내용 저장
        participationRepository.save(participation);

        // 응답 DTO 생성
        ParticipationUpdateStatusResponseDto responseDto = ParticipationUpdateStatusResponseDto.from(participation);
        return responseDto;

    }
}
