package com.carpBread.shareEatIt.domain.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
public class SignUpResponseDto {
    private Long id;
    private String username;

    public SignUpResponseDto(Long id, String username) {
        this.id = id;
        this.username = username;
    }
}
