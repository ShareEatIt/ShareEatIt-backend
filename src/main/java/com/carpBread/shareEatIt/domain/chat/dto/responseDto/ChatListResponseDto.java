package com.carpBread.shareEatIt.domain.chat.dto.responseDto;

import lombok.Getter;

import java.util.List;

@Getter
public class ChatListResponseDto {
    private final List<ChatMessageResponseDto> chatList;

    public ChatListResponseDto(List<ChatMessageResponseDto> chatList){
        this.chatList = chatList;
    }
}

