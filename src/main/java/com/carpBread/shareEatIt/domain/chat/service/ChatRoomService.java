package com.carpBread.shareEatIt.domain.chat.service;

import com.carpBread.shareEatIt.domain.chat.dto.responseDto.ChatRoomListDetailResponseDto;
import com.carpBread.shareEatIt.domain.chat.dto.responseDto.ChatRoomListResponseDto;
import com.carpBread.shareEatIt.domain.chat.dto.responseDto.ChatRoomResponseDto;
import com.carpBread.shareEatIt.domain.chat.entity.ChatRoom;
import com.carpBread.shareEatIt.domain.chat.entity.ChatRoomStatus;
import com.carpBread.shareEatIt.domain.chat.repository.ChatRoomRepository;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.participation.entity.Participation;
import com.carpBread.shareEatIt.domain.participation.repository.ParticipationRepository;
import com.carpBread.shareEatIt.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.carpBread.shareEatIt.global.exception.CustomExceptionStatus.*;
import static com.carpBread.shareEatIt.global.exception.Domain.CHAT;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatRoomService {

    private final ParticipationRepository participationRepository;
    private final ChatRoomRepository chatRoomRepository;

    /* 채팅방 생성 */
    public ChatRoom createChatRoom(Member member, Long participationId) {

        // Participation 객체 찾기
        Participation participation = participationRepository.findById(participationId)
                .orElseThrow(() ->  new CustomException(NOT_FOUND_PARTICIPATION, "해당 Id의 participation을 찾을 수 없습니다.", "ChatRoomService", "participationId: " + participationId, CHAT));

        // 채팅방 객체 생성
        ChatRoom chatRoom = ChatRoom.builder()
                .participation(participation)
                .status(ChatRoomStatus.ACTIVE)
                .build();

        // 저장 & 반환
        return chatRoomRepository.save(chatRoom);

    }

    // 채팅방 생성 dto로 반환
    public ChatRoomResponseDto changeChatRoomToDto(ChatRoom chatRoom){
        // 응답 dto로 반환
        return ChatRoomResponseDto.from(chatRoom);
    }


    /* 채팅방 목록 조회 */
    public ChatRoomListResponseDto findAllChatRoom(Member member) {

        // 사용자가 참여한 채팅방 목록 조회
        List<ChatRoom> chatRoomList = chatRoomRepository.findByUserAndStatus(member.getId());

        // DTO 리스트 변환: 각 채팅방마다 상대방을 포함
        List<ChatRoomListDetailResponseDto> dtoList = chatRoomList.stream()
                .map(chatRoom -> {
                    Member opponent = findOpponent(member.getId(), chatRoom);
                    return ChatRoomListDetailResponseDto.from(chatRoom, opponent);
                })
                .collect(Collectors.toList());

        return new ChatRoomListResponseDto(dtoList);
    }

    // 특정 채팅방에서 상대방 찾기
    private Member findOpponent(Long memberId, ChatRoom chatRoom) {
        if (!chatRoom.getParticipation().getGiver().getId().equals(memberId)) {
            return chatRoom.getParticipation().getGiver();
        } else if (!chatRoom.getParticipation().getReceiver().getId().equals(memberId)) {
            return chatRoom.getParticipation().getReceiver();
        }
        throw new CustomException(NOT_FOUND_OPPONENT, "채팅방에 상대방이 존재하지 않습니다.", "ChatRoomService", null, CHAT);
    }


    /* 채팅방 상태 변경 */
    public ChatRoomResponseDto updateChatRoomStatus(Member member, Long chatRoomId) {

        // chatRoom 객체 찾아오기
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(()-> new CustomException(NOT_FOUND_CHATROOM, "해당 Id의 채팅방을 찾을수 없습니다.", "ChatRoomService", "chatRoomId: "+ chatRoomId, CHAT));
        // 상태 변경
        chatRoom.updateStatus();
        // 변경 내용 저장
        chatRoomRepository.save(chatRoom);

        // 참여테이블의 정보도 수정
        UpdateMemberIsNotInChat(member.getId(), chatRoom);

        // 응답 dto 생성
        ChatRoomResponseDto responseDto = ChatRoomResponseDto.from(chatRoom);
        return responseDto;

    }

    // 참여테이블에서 해당 사용자 상태도 변경
    public void UpdateMemberIsNotInChat(Long memberId, ChatRoom chatRoom){

        Participation pt = chatRoom.getParticipation();
        Long giverId = pt.getGiver().getId();
        Long receiverId = pt.getReceiver().getId();

        // 사용자가 해당 참여 테이블의 giver인지 receiver인지 확인
        if(memberId.equals(giverId)){
            pt.updateIsGiverInChat();
        } else if (memberId.equals(receiverId)) {
            pt.updateIsReceiverInChat();
        }

    }
}
