package com.carpBread.shareEatIt.domain.chat.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ChatMessageType {
    ENTER, TALK, LEAVE;
}
