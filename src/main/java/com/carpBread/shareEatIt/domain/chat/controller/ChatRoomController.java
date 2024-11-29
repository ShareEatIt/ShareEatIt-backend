package com.carpBread.shareEatIt.domain.chat.controller;

import com.carpBread.shareEatIt.domain.auth.AuthUser;
import com.carpBread.shareEatIt.domain.chat.dto.ChatRoomListResponseDto;
import com.carpBread.shareEatIt.domain.chat.dto.ChatRoomResponseDto;
import com.carpBread.shareEatIt.domain.chat.entity.ChatRoom;
import com.carpBread.shareEatIt.domain.chat.service.ChatRoomService;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chatRoom")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    /* 채팅방 생성 */
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse<ChatRoomResponseDto>> createChatRoom(@AuthUser Member member,
                                                                           @RequestParam(name = "ptId")Long participationId){
        ChatRoom savedChatRoom = chatRoomService.createChatRoom(member, participationId);
        ChatRoomResponseDto responseDto = chatRoomService.changeChatRoomToDto(savedChatRoom);
        ApiResponse<ChatRoomResponseDto> response = new ApiResponse<>(
                HttpStatus.CREATED.value(),
                "채팅방 생성",
                responseDto
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /* 채팅방 목록 조회 */
    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse<ChatRoomListResponseDto>> getAllChatRooms(@AuthUser Member member) {

        ChatRoomListResponseDto responseDto = chatRoomService.findAllChatRoom(member);
        ApiResponse<ChatRoomListResponseDto> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "사용자의 모든 채팅방 조회",
                responseDto
        );
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /* 채팅방 나가기 */
    @PatchMapping("/{chatRoomId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse<ChatRoomResponseDto>> existChatRoom(@AuthUser Member member,
                                                                          @PathVariable(name = "chatRoomId") Long chatRoomId) {
        ChatRoomResponseDto responseDto = chatRoomService.updateChatRoomStatus(member, chatRoomId);
        ApiResponse<ChatRoomResponseDto> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "채팅방 나가기 설공",
                responseDto
        );
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
