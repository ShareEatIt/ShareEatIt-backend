package com.carpBread.shareEatIt.domain.chat.service;

import com.carpBread.shareEatIt.domain.chat.dto.ChatRoomListResponseDto;
import com.carpBread.shareEatIt.domain.chat.dto.ChatRoomResponseDto;
import com.carpBread.shareEatIt.domain.chat.entity.ChatRoom;
import com.carpBread.shareEatIt.domain.chat.entity.ChatRoomStatus;
import com.carpBread.shareEatIt.domain.chat.repository.ChatRoomRepository;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.participation.entity.Participation;
import com.carpBread.shareEatIt.domain.participation.repository.ParticipationRepository;
import com.carpBread.shareEatIt.global.exception.AppException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.carpBread.shareEatIt.global.exception.ErrorCode.NOT_FOUND_CHATROOM;
import static com.carpBread.shareEatIt.global.exception.ErrorCode.NOT_FOUND_PARTICIPATION;

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
                .orElseThrow(() -> new AppException(NOT_FOUND_PARTICIPATION, "해당 Id의 participation을 찾을 수 없습니다.", "/chatRoom?" + participationId));

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

        // 사용자가 == giver 이면서 giverStatus가 true이거나 receiver이면서 receiverStatus= true인 참여 객체의 채팅방 조회
        List<ChatRoom> chatRoomList = chatRoomRepository.findByUserAndStatus(member.getId());

        List<ChatRoomResponseDto> dtoList = convertDtoToList(chatRoomList);
        return new ChatRoomListResponseDto(dtoList);
    }

    // list를 dto로 변환
    private List<ChatRoomResponseDto> convertDtoToList(List<ChatRoom> chatRoomList){
        return chatRoomList.stream()
                .map(ChatRoomResponseDto::from)
                .collect(Collectors.toList());

    }


    /* 채팅방 상태 변경 */
    public ChatRoomResponseDto updateChatRoomStatus(Member member, Long chatRoomId) {

        // chatRoom 객체 찾아오기
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(()-> new AppException(NOT_FOUND_CHATROOM, "해당 Id의 채팅방을 찾을수 없습니다." , "/chatRoom/" + chatRoomId));
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
