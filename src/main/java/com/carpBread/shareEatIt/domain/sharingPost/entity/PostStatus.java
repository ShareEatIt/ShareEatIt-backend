package com.carpBread.shareEatIt.domain.sharingPost.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PostStatus {
    AVAILABLE, FAVORITE, CHATTING, COMPLETED
}
