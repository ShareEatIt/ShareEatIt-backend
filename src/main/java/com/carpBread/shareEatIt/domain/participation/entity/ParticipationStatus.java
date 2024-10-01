package com.carpBread.shareEatIt.domain.participation.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ParticipationStatus {
    CHATTING, FAVORITE,CANCELED, MATCHED, COMPLETED
}
