package com.carpBread.shareEatIt.domain.sharingPost.dto.response;

import com.carpBread.shareEatIt.domain.member.dto.response.LocationResponseDtoComponent;
import com.carpBread.shareEatIt.domain.member.dto.response.MemberAsWriterSimpleDtoComponent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
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


    public SharingPostCreateResponseDto(Long id, MemberAsWriterSimpleDtoComponent writer,
                                        String title, String category, Boolean isFinished,
                                        String foodName, String status, String postType,
                                        LocalDate expDate, LocalDate purchaseDate,
                                        List<String> imgList, LocationResponseDtoComponent location,
                                        String kakaoLocationCode, String description,
                                        LocalDateTime endAt, LocalDateTime createdAt) {
        this.id = id;
        this.writer = writer;
        this.title = title;
        this.category = category;
        this.isFinished = isFinished;
        this.foodName = foodName;
        this.status = status;
        this.postType = postType;
        this.expDate = expDate;
        this.purchaseDate = purchaseDate;
        this.imgList = imgList;
        this.location = location;
        this.kakaoLocationCode = kakaoLocationCode;
        this.description = description;
        this.endAt = endAt;
        this.createdAt = createdAt;
    }
}
