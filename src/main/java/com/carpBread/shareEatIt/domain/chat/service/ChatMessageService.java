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
    public ChatMessageResponseDto saveMessage(ChatMessageRequestDto requestDto) {

        // requestDto로 받아온 roomId로 채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findById(requestDto.getChatRoomId())
                .orElseThrow(() -> new AppException(NOT_FOUND_CHATROOM, "해당 Id의 채팅방을 찾을수 없습니다." , "/chat/message"));

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
}
