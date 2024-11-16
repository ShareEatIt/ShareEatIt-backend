package com.carpBread.shareEatIt.domain.sharingPost.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Getter
public class SharingPostUpdateRequestDto {
    @NotNull
    private String title;

    @NotNull
    private String category;

    @NotNull
    // 완제품인지 식료품인지에 대한 여부입니다. true -> 완제품(조리된 적이 있는 식품) / false -> 식료품(조리되지 않은 식자재)
    private Boolean isFinished;

    @NotNull
    private String foodName;

    @NotNull
    private LocalDate expDate;

    @Nullable
    private LocalDate purchaseDate;


    /*************** 위치 관련  ****************/

    @NotNull
    private String addressSt;

    @Nullable
    private String addressDetail;

    // 카카오지도 location
    @Nullable
    private String kakaoLocationCode;

    @NotNull
    private Double latitude;
    @NotNull
    private Double longitude;

    @Nullable
    private List<String> imgUrlList;

    @Nullable
    private String description;

    @NotNull
    private String postType;

    @NotNull
    private LocalDateTime endAt;
}
