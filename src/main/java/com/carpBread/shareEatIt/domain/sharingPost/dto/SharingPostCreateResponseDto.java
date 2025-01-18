package com.carpBread.shareEatIt.domain.sharingPost.dto;

import com.carpBread.shareEatIt.domain.member.dto.response.LocationResponseDtoComponent;
import com.carpBread.shareEatIt.domain.member.dto.MemberAsWriterSimpleDtoComponent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Builder @Getter
public class SharingPostCreateResponseDto {

    private Long id;
    private MemberAsWriterSimpleDtoComponent writer;
    private String title;
    private String category;
    private Boolean isFinished;
    private String foodName;
    private String status;
    private String postType;
    private LocalDate expDate;
    private LocalDate purchaseDate;
    private List<String> imgList;
    private LocationResponseDtoComponent location;
    private String kakaoLocationCode;
    private String description;
    private LocalDateTime endAt;
    private LocalDateTime createdAt;

}
