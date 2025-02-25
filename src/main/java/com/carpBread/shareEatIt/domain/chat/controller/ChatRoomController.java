package com.carpBread.shareEatIt.domain.chat.controller;

import com.carpBread.shareEatIt.domain.auth.annotation.AuthUser;
import com.carpBread.shareEatIt.domain.chat.dto.responseDto.ChatRoomListResponseDto;
import com.carpBread.shareEatIt.domain.chat.dto.responseDto.ChatRoomResponseDto;
import com.carpBread.shareEatIt.domain.chat.entity.ChatRoom;
import com.carpBread.shareEatIt.domain.chat.service.ChatRoomService;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chatRoom")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    /* 채팅방 생성 */
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ChatRoomResponseDto> createChatRoom(@AuthUser Member member,
                                                           @RequestParam(name = "ptId")Long participationId){
        ChatRoom savedChatRoom = chatRoomService.createChatRoom(member, participationId);
        ChatRoomResponseDto responseDto = chatRoomService.changeChatRoomToDto(savedChatRoom);
        return new ApiResponse<>(
                HttpStatus.CREATED.value(),
                "채팅방 생성",
                responseDto
        );
    }

    /* 채팅방 목록 조회 */
    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<ChatRoomListResponseDto> getAllChatRooms(@AuthUser Member member) {

        ChatRoomListResponseDto responseDto = chatRoomService.findAllChatRoom(member);
        return new ApiResponse<>(
                HttpStatus.OK.value(),
                "사용자의 모든 채팅방 조회",
                responseDto
        );
    }

    /* 채팅방 나가기 */
    @PatchMapping("/{chatRoomId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<ChatRoomResponseDto> existChatRoom(@AuthUser Member member,
                                                          @PathVariable(name = "chatRoomId") Long chatRoomId) {
        ChatRoomResponseDto responseDto = chatRoomService.updateChatRoomStatus(member, chatRoomId);
        return new ApiResponse<>(
                HttpStatus.OK.value(),
                "채팅방 나가기 설공",
                responseDto
        );
    }
}
