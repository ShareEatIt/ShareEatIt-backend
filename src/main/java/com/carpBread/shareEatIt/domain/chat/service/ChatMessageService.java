package com.carpBread.shareEatIt.domain.chat.service;

import com.carpBread.shareEatIt.domain.chat.dto.ChatListResponseDto;
import com.carpBread.shareEatIt.domain.chat.dto.ChatMessageRequestDto;
import com.carpBread.shareEatIt.domain.chat.dto.ChatMessageResponseDto;
import com.carpBread.shareEatIt.domain.chat.entity.ChatMessage;
import com.carpBread.shareEatIt.domain.chat.entity.ChatRoom;
import com.carpBread.shareEatIt.domain.chat.repository.ChatMessageRepository;
import com.carpBread.shareEatIt.domain.chat.repository.ChatRoomRepository;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.member.repository.MemberRepository;
import com.carpBread.shareEatIt.domain.notice.dto.NoticeCreateDto;
import com.carpBread.shareEatIt.domain.notice.dto.NoticeRelatedObjectResponseComponent;
import com.carpBread.shareEatIt.domain.notice.entity.Notice;
import com.carpBread.shareEatIt.domain.notice.entity.NoticeType;
import com.carpBread.shareEatIt.domain.notice.repository.NoticeRepository;
import com.carpBread.shareEatIt.domain.notice.service.SseService;
import com.carpBread.shareEatIt.domain.participation.entity.Participation;
import com.carpBread.shareEatIt.domain.participation.repository.ParticipationRepository;
import com.carpBread.shareEatIt.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.carpBread.shareEatIt.global.exception.CustomExceptionStatus.NOT_FOUND_CHATROOM;
import static com.carpBread.shareEatIt.global.exception.CustomExceptionStatus.NOT_MEMBER_OF_CHATROOM;
import static com.carpBread.shareEatIt.global.exception.Domain.CHAT;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ParticipationRepository participationRepository;
    private final MemberRepository memberRepository;
    private final SseService sseService;
    private final NoticeRepository noticeRepository;

    /* 채팅 메시지 저장 */
    public ChatMessageResponseDto saveMessage(ChatMessageRequestDto requestDto) {

        // requestDto로 받아온 roomId로 채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findById(requestDto.getChatRoomId())
                .orElseThrow(() -> new CustomException(NOT_FOUND_CHATROOM, "해당 Id의 채팅방을 찾을수 없습니다.", "ChatMessageService", "/chat/message", CHAT));

        // chatMessage 객체 생성
        ChatMessage message = ChatMessage.builder()
                .type(requestDto.getType())
                .chatRoomId(requestDto.getChatRoomId())
                .senderId(requestDto.getSenderId())
                .content(requestDto.getContent())
                .createdAt(LocalDateTime.now())
                .build();

        // 객체 저장
        ChatMessage savedMessage = chatMessageRepository.save(message);

        sendNotification(savedMessage);

        // 응답 DTO 생성
        ChatMessageResponseDto responseDto = ChatMessageResponseDto.from(savedMessage);
        return responseDto;

    }


    /* 해당 채팅방 메시지 조회 */
    public ChatListResponseDto getMessageByChatRoomId(Member member, Long chatRoomId) {

        // 해당 채팅방에 속한 사람인지 확인
        if (!chatRoomRepository.existsByMemberInChatRoom(member.getId(), chatRoomId)){
            throw new CustomException(NOT_MEMBER_OF_CHATROOM, "채팅방의 유저가 아니므로 접근할 수 없습니다.", "ChatMessageService", "/chat/message/" + chatRoomId, CHAT);
        }

        List<ChatMessage> chatMessageList = chatMessageRepository.findByChatRoomId(chatRoomId);
        long count = 0L;
        for (ChatMessage message : chatMessageList) {
            count++;
        }
        System.out.println(count);

        List<ChatMessageResponseDto> dtoList = convertDtoToList(chatMessageList);
        return new ChatListResponseDto(dtoList);
    }

    // list를 dto로 변환
    private List<ChatMessageResponseDto> convertDtoToList(List<ChatMessage> chatMessageList){
        return chatMessageList.stream()
                .map(ChatMessageResponseDto::from)
                .collect(Collectors.toList());

    }


    /* chatting 알람 보내기 */
    private void sendNotification(ChatMessage chat){
        // 사용자 탐색
        ChatRoom chatRoom = chatRoomRepository.findById(chat.getChatRoomId())
                .orElseThrow(() -> new CustomException(NOT_FOUND_CHATROOM, "해당 Id의 채팅방을 찾을수 없습니다." , "ChatMessageService", "/chat/message", CHAT));
        Participation participation = chatRoom.getParticipation();
        Member recipient;
        Member receiver = participation.getReceiver();
        Member giver = participation.getGiver();

        if(chat.getSenderId() == receiver.getId())
            recipient=receiver;
        else recipient=giver;

        // 검증. 받는 사람이 notice 설정을 하지 않은 경우 반환
        if(!sseService.isRegistered(recipient.getId()))
            return;

        // 알림 생성
        String title="채팅방에 메세지가 도착했습니다! 확인해보세요🗨️";
        String message = "["+participation.getPost().getTitle()+"] 게시글 채팅방에서 새로운 메세지가 도착했습니다."
                +"\n채팅방에서 답장을 남겨주세요🥰";
        Notice newNotice = Notice.builder()
                .title(title)
                .message(message)
                .member(recipient)
                .type(NoticeType.CHATTING)
                .isRead(false)
                .build();

        Notice savedNotice = noticeRepository.save(newNotice);

        NoticeRelatedObjectResponseComponent noticeObject=NoticeRelatedObjectResponseComponent.builder()
                .id(chatRoom.getId())
                .category(participation.getPost().getCategory().name())
                .build();

        NoticeCreateDto noticeDto = NoticeCreateDto.builder()
                .id(savedNotice.getId())
                .title(savedNotice.getTitle())
                .message(savedNotice.getMessage())
                .noticeType(NoticeType.CHATTING.name())
                .noticeObject(noticeObject)
                .createdAt(savedNotice.getCreatedAt())
                .build();

        // 알림 보내기
        sseService.sendNotification(recipient.getId(), noticeDto);

    }
}
