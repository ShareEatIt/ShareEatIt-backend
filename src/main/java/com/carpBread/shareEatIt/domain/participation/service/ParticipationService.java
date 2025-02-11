package com.carpBread.shareEatIt.domain.participation.service;

import com.carpBread.shareEatIt.domain.chat.entity.ChatRoom;
import com.carpBread.shareEatIt.domain.chat.repository.ChatRoomRepository;
import com.carpBread.shareEatIt.domain.chat.service.ChatRoomService;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.notice.dto.NoticeCreateDto;
import com.carpBread.shareEatIt.domain.notice.dto.NoticeRelatedObjectResponseComponent;
import com.carpBread.shareEatIt.domain.notice.entity.Notice;
import com.carpBread.shareEatIt.domain.notice.entity.NoticeType;
import com.carpBread.shareEatIt.domain.notice.repository.NoticeRepository;
import com.carpBread.shareEatIt.domain.notice.service.SseService;
import com.carpBread.shareEatIt.domain.participation.dto.requestDto.ParticipationRequestDto;
import com.carpBread.shareEatIt.domain.participation.dto.responseDto.ParticipationHistoryListResponseDto;
import com.carpBread.shareEatIt.domain.participation.dto.responseDto.ParticipationHistoryResponseDto;
import com.carpBread.shareEatIt.domain.participation.dto.responseDto.ParticipationResponseDto;
import com.carpBread.shareEatIt.domain.participation.dto.responseDto.ParticipationUpdateStatusResponseDto;
import com.carpBread.shareEatIt.domain.participation.entity.Participation;
import com.carpBread.shareEatIt.domain.participation.entity.ParticipationStatus;
import com.carpBread.shareEatIt.domain.participation.repository.GratitudeStickerRepository;
import com.carpBread.shareEatIt.domain.participation.repository.ParticipationRepository;
import com.carpBread.shareEatIt.domain.sharingPost.entity.PostImgUrl;
import com.carpBread.shareEatIt.domain.sharingPost.entity.PostStatus;
import com.carpBread.shareEatIt.domain.sharingPost.entity.PostType;
import com.carpBread.shareEatIt.domain.sharingPost.entity.SharingPost;
import com.carpBread.shareEatIt.domain.sharingPost.repository.SharingPostRepository;
import com.carpBread.shareEatIt.domain.sharingPost.service.SharingPostService;
import com.carpBread.shareEatIt.global.exception.CustomException;
import com.carpBread.shareEatIt.global.exception.CustomExceptionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.carpBread.shareEatIt.global.exception.CustomExceptionStatus.*;
import static com.carpBread.shareEatIt.global.exception.CustomExceptionStatus.CAN_NOT_PARTICIPATE_MY_POST;
import static com.carpBread.shareEatIt.global.exception.Domain.PARTICIPATION;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ParticipationService {

    private final ParticipationRepository participationRepository;
    private final SharingPostRepository sharingPostRepository;
    private final GratitudeStickerRepository gratitudeStickerRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final NoticeRepository noticeRepository;

    private final ChatRoomService chatRoomService;
    private final SseService sseService;
    private final SharingPostService sharingPostService;

    /* 참여 생성 - 나눔글 채팅 참여 */
    public Pair<HttpStatus, ParticipationResponseDto> createParticipation(Member receiver, ParticipationRequestDto requestDto) {

        Long postId= requestDto.getSharingPostId();

        // requestDto로 받아온 postId의 나눔글 조회
        SharingPost post = sharingPostRepository.findById(postId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_SHARINGPOST, "해당ID의 나눔글을 찾지 못했습니다.", "ParticipationService", "participationId: "+requestDto.getSharingPostId(), PARTICIPATION));

        // 참여하려는 사용자가 개설자가 아닌지 확인
        if(post.getWriter().getId().equals(receiver.getId())){
            throw new CustomException(CAN_NOT_PARTICIPATE_MY_POST, "본인의 나눔글에는 참여할 수 없습니다. ", "ParticipationService", null, PARTICIPATION);
        }

        // 이미 참여한 나눔인 경우 - 참여 기록 반환
        Participation existingParticipation = participationRepository.findByPostIdAndReceiverId(postId, receiver.getId());
        if (existingParticipation != null) {
            ChatRoom existingChatRoom = chatRoomRepository.findByParticipationId(existingParticipation.getId());
            return Pair.of(HttpStatus.OK, ParticipationResponseDto.from(existingParticipation, existingChatRoom));
        }

        // 참여 객체 생성
        Participation newParticipation = Participation.builder()
                .post(post)
                .status(ParticipationStatus.AVAILABLE)
                .giver(post.getWriter())
                .receiver(receiver)
                .isGiverInChat(true)
                .isReceiverInChat(true)
                .build();
        Participation savedParticipation = participationRepository.save(newParticipation);
        // 채팅방 생성
        ChatRoom savedChatRoom = chatRoomService.createChatRoom(receiver, savedParticipation.getId());

        return Pair.of(HttpStatus.CREATED, ParticipationResponseDto.from(savedParticipation, savedChatRoom));
    }


    /* 사용자가 나눔받은 모든 기록 조회 */
    public ParticipationHistoryListResponseDto findAllParticipation(Member receiver) {
        // 사용자 = receiver이고 나눔완료 상태인 모든 '참여'의 나눔글 조회
        List<ParticipationHistoryResponseDto> dtoList = participationRepository.findSharingPostByUserAndStatus(receiver.getId())
                .stream()
                .map(this::convertToDtoWithFirstImg) // 첫 번째 이미지를 포함해 DTO로 변환
                .collect(Collectors.toList());
        return new ParticipationHistoryListResponseDto(dtoList);
    }


    /* 사용자가 특정 provider의 나눔을 받은 모든 기록 조회 */
    public ParticipationHistoryListResponseDto findAllParticipationByProvider(Member receiver, PostType provider) {
        // 사용자 = receiver이고 상태 = 나눔완료인 모든 '참여'의 나눔글 중 특정 provider의 글 조회
        List<ParticipationHistoryResponseDto> dtoList = participationRepository.findSharingPostByUserAndStatusAndPostType(receiver.getId(), provider)
                .stream()
                .map(this::convertToDtoWithFirstImg) // 첫 번째 이미지를 포함해 DTO로 변환
                .collect(Collectors.toList());
        return new ParticipationHistoryListResponseDto(dtoList);
    }


    // 게시글을 DTO로 변환하며 첫 번째 이미지를 추가
    private ParticipationHistoryResponseDto convertToDtoWithFirstImg(SharingPost post) {
        String firstImgUrl = findFirstImgUrl(post); // 첫 번째 이미지 조회
        return ParticipationHistoryResponseDto.from(post, firstImgUrl); // DTO 생성 시 이미지 URL 추가
    }

    // 게시글에서 첫 번째 이미지 URL 조회
    private String findFirstImgUrl(SharingPost post) {
        return sharingPostService.getPostImgUrlList(post).stream()
                .filter(imgUrl -> imgUrl.getImgOrder() == 1) // imgOrder가 1인 이미지 필터링
                .map(PostImgUrl::getUrl) // URL만 추출
                .findFirst() // 첫 번째 URL 가져오기
                .orElseThrow(() -> new CustomException(CustomExceptionStatus.NOT_FOUND_POST_IMAGE, "ParticipationService", "현재 POST에 해당하는 IMAGE를 찾을 수 없습니다", null, PARTICIPATION));
    }


    /* 참여 상태 변경 */
    public ParticipationUpdateStatusResponseDto updateStatus(Long ptId, Member giver, ParticipationStatus ptStatus) {

        // participation 객체 찾아오기
        Participation participation = participationRepository.findById(ptId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_PARTICIPATION, "해당 ID의 참여기록을 찾지 못했습니다.", "ParticipationService", "participationId: "+ptId, PARTICIPATION));

        String sharingPostStatus = participation.getPost().getStatus().toString();
        String participationStatus = ptStatus.toString();

        // 검증1: 해당 참여의 나눔글과 동일한 상태로 변경하려는 상태인지 확인 (이미 찜 or 나눔완료 된 나눔글의 참여인 경우)
        if (sharingPostStatus.equals(participationStatus)){
            log.warn("이미 나눔글이 {}인 상태로, 같은 상태로 변경 불가", sharingPostStatus);
            if (sharingPostStatus.equals("COMPLETED")){
                throw new CustomException(ALREADY_COMPLETED_SHARINGPOST, "이미 나눔 완료된 나눔입니다.", "ParticipationService", null, PARTICIPATION);
            }
            else if (sharingPostStatus.equals("MATCHED")){
                throw new CustomException(ALREADY_MATCHED_SHARINGPOST, "이미 찜 상태인 나눔입니다.", "ParticipationService", null, PARTICIPATION);
            }
            else {
                throw new CustomException(ALREADY_AVAILABLE_SHARINGPSOT, "현재 나눔 가능한 상태로 변경할 상태가 없습니다.", "ParticipationService", null, PARTICIPATION);
            }
        }

        // 검증2: 사용자가 나눔자의 writer인지 확인
        if (!giver.getId().equals(participation.getPost().getWriter().getId())){
            log.warn("사용자 != 나눔글 작성자");
            log.info("giverId : {}", giver.getId());
            log.info("writerId : {}", participation.getPost().getWriter().getId());
            throw new CustomException(NOT_WRITER_OF_SHARINGPOST, "나눔글 작성자가 아니므로 나눔 상태를 변경할 수 없습니다.", "ParticipationService", null, PARTICIPATION);
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
            log.warn("잘못된 상태값으로, 해당 나눔글의 상태 변경에 실패");
            throw new CustomException(INVALID_STATUS_VALUE ,"잘못된 상태값으로, 해당 나눔글의 상태 변경에 실패하였습니다.", "ParticipationService", null, PARTICIPATION);
        }

        // 변경한 내용 저장
        participationRepository.save(participation);

        // 알림 보내기
        sendNotification(participation, ptStatus);

        // 응답 DTO 생성
        ParticipationUpdateStatusResponseDto responseDto = ParticipationUpdateStatusResponseDto.from(participation);
        return responseDto;

    }


    /* review notice 보내기 */
    private void sendNotification(Participation participation,ParticipationStatus status){
        // 검증 1. 참여자가 Notice 설정을 하지 않은 경우 반환
        if (!sseService.isRegistered(participation.getReceiver().getId()))
            return;

        // 검증 2. Participation 상태가 COMPLETED가 아닌 경우 반환
        if (status!=ParticipationStatus.COMPLETED)
            return;

        // 검증 3. Gratitude Sticker, 반응이 완료된 상태이면 반환
        Boolean isExists = gratitudeStickerRepository.existsByPost(participation.getPost());
        if (isExists)
            return;


        // 알림 생성
        String title = "나눔이 완료되었습니다! 후기를 남겨주세요😺";
        String message = participation.getGiver().getNickname()+"님과의 "+participation.getPost().getFoodName()+" 나눔이 완료되었습니다! "
                +"\n나눔글 페이지에서 후기를 남겨주세요❤️";
        Notice newNotice = Notice.builder()
                .title(title)
                .message(message)
                .member(participation.getReceiver())
                .type(NoticeType.REVIEW)
                .isRead(false)
                .build();

        Notice savedNotice = noticeRepository.save(newNotice);

        NoticeRelatedObjectResponseComponent noticeObject = NoticeRelatedObjectResponseComponent.builder()
                .id(participation.getPost().getId())
                .category(participation.getPost().getCategory().name())
                .build();

        NoticeCreateDto noticeDto = NoticeCreateDto.builder()
                .id(savedNotice.getId())
                .title(savedNotice.getTitle())
                .message(savedNotice.getMessage())
                .noticeType(NoticeType.REVIEW.name())
                .noticeObject(noticeObject)
                .createdAt(savedNotice.getCreatedAt())
                .build();

        // 알림 보내기
        sseService.sendNotification(participation.getReceiver().getId(), noticeDto);


    }

}
