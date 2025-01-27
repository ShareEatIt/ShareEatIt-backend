package com.carpBread.shareEatIt.domain.sharingPost.dto;

import com.carpBread.shareEatIt.domain.member.dto.response.LocationResponseDtoComponent;
import com.carpBread.shareEatIt.domain.member.dto.response.MemberAsWriterSimpleDtoComponent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Builder @Getter
public class SharingPostResponseDto {

    private Long id;
    private String title;
    private List<String> imgList;
    private String category;
    private Boolean isFinished;
    private String foodName;
    private LocalDate expDate;
    private LocalDate purchaseDate;
    private LocationResponseDtoComponent location;
    private LocalDateTime endAt;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private MemberAsWriterSimpleDtoComponent writer;
    private String postType;
    private String description;
    private String status;
    private String subject;
    private String gratitudeSticker;

}
