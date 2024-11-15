package com.carpBread.shareEatIt.domain.chat.dto;

import com.carpBread.shareEatIt.domain.chat.entity.ChatMessageType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatMessageRequestDto {

    @NotNull
    private ChatMessageType type;

    @NotNull
    private String content;

}
