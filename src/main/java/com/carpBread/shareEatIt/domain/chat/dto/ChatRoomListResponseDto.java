package com.carpBread.shareEatIt.domain.chat.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class ChatRoomListResponseDto {

    private final List<ChatRoomResponseDto> chatRoomList;

    public ChatRoomListResponseDto(List<ChatRoomResponseDto> chatRooms){
        this.chatRoomList = chatRooms;
    }
}
