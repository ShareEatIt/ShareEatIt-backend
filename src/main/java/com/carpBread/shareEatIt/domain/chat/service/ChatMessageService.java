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
import com.carpBread.shareEatIt.domain.participation.repository.ParticipationRepository;
import com.carpBread.shareEatIt.global.exception.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.carpBread.shareEatIt.global.exception.ErrorCode.NOT_FOUND_CHATROOM;
import static com.carpBread.shareEatIt.global.exception.ErrorCode.NOT_MEMBER_OF_CHATROOM;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ParticipationRepository participationRepository;
    private final MemberRepository memberRepository;

    /* 채팅 메시지 저장 */
    public ChatMessageResponseDto saveMessage(Member sender, Long roomId, ChatMessageRequestDto requestDto) {

        // requestDto로 받아온 roomId로 채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new AppException(NOT_FOUND_CHATROOM, "해당 Id의 채팅방을 찾을수 없습니다." , "/chat/" + roomId));

        // chatMessage 객체 생성
        ChatMessage message = ChatMessage.builder()
                .type(requestDto.getType())
                .chatRoomId(roomId)
                .senderId(sender.getId())
                .content(requestDto.getContent())
                .createdAt(LocalDateTime.now())
                .build();
        log.info("채팅방 번호 = {} " , message.getChatRoomId());
        log.info("메시지 내용 = {} " , message.getContent());


        // 객체 저장
        ChatMessage savedMessage = chatMessageRepository.save(message);
        log.info("저장된 후 채팅방 번호 = {} " , savedMessage.getChatRoomId());
        log.info("메시지 내용 = {} " , savedMessage.getContent());
        System.out.println(chatMessageRepository.findById("3"));

        // 응답 DTO 생성
        ChatMessageResponseDto responseDto = ChatMessageResponseDto.from(savedMessage);
        return responseDto;

    }


    /* 해당 채팅방 메시지 조회 */
    public ChatListResponseDto getMessageByChatRoomId(Member member, Long chatRoomId) {

        // 해당 채팅방에 속한 사람인지 확인
        if (!chatRoomRepository.existsByMemberInChatRoom(member.getId(), chatRoomId)){
            throw new AppException(NOT_MEMBER_OF_CHATROOM, "채팅방의 유저가 아니므로 접근할 수 없습니다.", "/chat/message/" + chatRoomId);
        }

        List<ChatMessage> chatMessageList = chatMessageRepository.findByChatRoomId(chatRoomId);

        List<ChatMessageResponseDto> dtoList = convertDtoToList(chatMessageList);
        return new ChatListResponseDto(dtoList);
    }

    // list를 dto로 변환
    private List<ChatMessageResponseDto> convertDtoToList(List<ChatMessage> chatMessageList){
        return chatMessageList.stream()
                .map(ChatMessageResponseDto::from)
                .collect(Collectors.toList());

    }
}
