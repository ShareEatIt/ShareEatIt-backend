package com.carpBread.shareEatIt.domain.sharingPost.dto;

import com.carpBread.shareEatIt.domain.member.dto.response.LocationResponseDtoComponent;
import com.carpBread.shareEatIt.domain.member.dto.response.MemberAsWriterSimpleDtoComponent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
@Getter
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

    public SharingPostResponseDto(Long id, String title,
                                  List<String> imgList, String category,
                                  Boolean isFinished, String foodName,
                                  LocalDate expDate, LocalDate purchaseDate,
                                  LocationResponseDtoComponent location,
                                  LocalDateTime endAt, LocalDateTime createdAt,
                                  LocalDateTime modifiedAt,
                                  MemberAsWriterSimpleDtoComponent writer,
                                  String postType, String description,
                                  String status, String subject, String gratitudeSticker) {
        this.id = id;
        this.title = title;
        this.imgList = imgList;
        this.category = category;
        this.isFinished = isFinished;
        this.foodName = foodName;
        this.expDate = expDate;
        this.purchaseDate = purchaseDate;
        this.location = location;
        this.endAt = endAt;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.writer = writer;
        this.postType = postType;
        this.description = description;
        this.status = status;
        this.subject = subject;
        this.gratitudeSticker = gratitudeSticker;
    }
}
