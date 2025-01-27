package com.carpBread.shareEatIt.domain.chat.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class ChatRoomListResponseDto {

    private final List<ChatRoomListDetailResponseDto> chatRoomList;

    public ChatRoomListResponseDto(List<ChatRoomListDetailResponseDto> chatRooms){
        this.chatRoomList = chatRooms;
    }
}
