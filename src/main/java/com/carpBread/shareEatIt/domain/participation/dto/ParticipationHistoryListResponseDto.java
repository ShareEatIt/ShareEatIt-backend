package com.carpBread.shareEatIt.domain.participation.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class ParticipationHistoryListResponseDto {
    private final List<ParticipationHistoryResponseDto> participatedPosts;

    public ParticipationHistoryListResponseDto(List<ParticipationHistoryResponseDto> posts) {
        this.participatedPosts = posts;
    }
}
