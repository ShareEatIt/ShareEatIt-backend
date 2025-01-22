package com.carpBread.shareEatIt.domain.sharingPost.service;

import com.carpBread.shareEatIt.domain.member.dto.response.LocationResponseDtoComponent;
import com.carpBread.shareEatIt.domain.sharingPost.dto.map.MapListResponseDto;
import com.carpBread.shareEatIt.domain.sharingPost.dto.map.MapRequestDto;
import com.carpBread.shareEatIt.domain.sharingPost.dto.map.MapResponseComponent;
import com.carpBread.shareEatIt.domain.sharingPost.entity.SharingPost;
import com.carpBread.shareEatIt.domain.sharingPost.repository.SharingPostQuerydslRepository;
import com.carpBread.shareEatIt.global.exception.CustomException;
import com.carpBread.shareEatIt.global.exception.CustomExceptionStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/* 지도 관련 service */
@Service
@RequiredArgsConstructor
public class MapService {

    // 키워드 알람 설정 1km
    private final double mapRadius = 50000;

    // repository
    private final SharingPostQuerydslRepository sharingPostQuerydslRepository;

    /* 지도 위 나눔글 리스트 조회 */
    @Transactional
    public MapListResponseDto getMapList(MapRequestDto dto) {

        // 점검 : MySQL 8.4 Reference Manual 에 정의된 메뉴얼에 따라, latitude(위도)는 [-90.0, 90.0] / longitude(경도)는 [-180.0, 180.0] 범위로 지정
        if ((dto.getLatitude()>90.0 || dto.getLatitude()<-90.0)
                || (dto.getLongitude()>180.0 || dto.getLongitude()<-180.0)){
            throw new CustomException(CustomExceptionStatus.VALUE_OUT_OF_RANGE,"입력한 위도 혹은 경도 값이 범위를 초과하거나 미만입니다. 범위를 재점검해주십시오.","/map/list");
        }

        // 나눔글 리스트 조회
        List<SharingPost> sharingPostsWithinRadius = sharingPostQuerydslRepository.findSharingPostsWithinRadius(dto.getLatitude(), dto.getLongitude(), mapRadius);

        // map response component list 생성
        List<MapResponseComponent> componentList = generateMapResponseComponentList(sharingPostsWithinRadius);

        return MapListResponseDto.builder()
                .mapList(componentList)
                .build();

    }

    /* MapResponseComponent list 생성 */
    private List<MapResponseComponent> generateMapResponseComponentList(List<SharingPost> sharingPostsWithinRadius){
        // response dto list component 생성
        List<MapResponseComponent> componentList = new ArrayList<>();
        for (SharingPost post : sharingPostsWithinRadius){
            LocationResponseDtoComponent location = LocationResponseDtoComponent.builder()
                    .latitude(post.getLocationPoint().getY())
                    .longitude(post.getLocationPoint().getX())
                    .addressDetail(post.getAddressDetail())
                    .addressSt(post.getAddressSt())
                    .build();


            MapResponseComponent component = MapResponseComponent.builder()
                    .kakaoLocationCode(post.getKakaoLocationCode())
                    .id(post.getId())
                    .category(post.getCategory().name())
                    .location(location)
                    .build();

            componentList.add(component);


        }
        return componentList;
    }
}