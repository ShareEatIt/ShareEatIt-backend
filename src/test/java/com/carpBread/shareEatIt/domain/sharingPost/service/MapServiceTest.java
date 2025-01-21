package com.carpBread.shareEatIt.domain.sharingPost.service;

import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.member.entity.Provider;
import com.carpBread.shareEatIt.domain.sharingPost.dto.map.MapListResponseDto;
import com.carpBread.shareEatIt.domain.sharingPost.dto.map.MapRequestDto;
import com.carpBread.shareEatIt.domain.sharingPost.entity.PostCategory;
import com.carpBread.shareEatIt.domain.sharingPost.entity.PostStatus;
import com.carpBread.shareEatIt.domain.sharingPost.entity.PostType;
import com.carpBread.shareEatIt.domain.sharingPost.entity.SharingPost;
import com.carpBread.shareEatIt.domain.sharingPost.repository.SharingPostRepository;
import com.carpBread.shareEatIt.global.exception.AppException;
import com.carpBread.shareEatIt.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/* MapService 단위 테스트*/
@ExtendWith(MockitoExtension.class)
class MapServiceTest {

    @InjectMocks
    private MapService mapService;

    @Mock
    private SharingPostRepository sharingPostRepository;

    @Test
    @DisplayName("성공 : 지도 위 나눔글 리스트 조회 성공 단위 테스트")
    void getMapListSuccessTest() {
        // given
        // 1. map request dto
        MapRequestDto mapRequestDto = generateMapRequestDto(127.099492,36.798331);

        // 2. sharingPostRepository findSharingPostsWithinRadius 리턴 값 설정
        Point point = generatePoint();
        Member testMember = createTestMember(Provider.INDIVIDUAL);
        SharingPost post = generateSharingPost(PostType.INDIVIDUAL, point, testMember);
        ArrayList<SharingPost> postList = new ArrayList<>();
        postList.add(post);
        Mockito.doReturn(postList).when(sharingPostRepository)
                .findSharingPostsWithinRadius(Mockito.anyDouble(), Mockito.anyDouble(),
                        Mockito.anyDouble());

        // when
        MapListResponseDto responseDto = mapService.getMapList(mapRequestDto);

        // then
        assertThat(responseDto.getMapList().get(0).getKakaoLocationCode()).isEqualTo(post.getKakaoLocationCode());


    }

    @Test
    @DisplayName("실패 : 범위에 맞지 않는 위도/경도 입력 시 AppException throw 하는 실패 테스트")
    void getMapListFailTest(){
        // given
        // 1. map request dto
        MapRequestDto mapRequestDto = generateMapRequestDto(1000.00, 190.00);

        // when
        AppException thrownException = assertThrows(AppException.class, () -> mapService.getMapList(mapRequestDto));

        // then
        assertThat(thrownException.getErrorCode()).isEqualTo(ErrorCode.VALUE_OUT_OF_RANGE);


    }

    /* TEST MEMBER 객체 생성 */
    private Member createTestMember(Provider provider){
        Point memberLocation =new GeometryFactory().createPoint(
                new Coordinate(127.099491,36.798330)
        );
        memberLocation.setSRID(4326);
        return Member.builder()
                .id(1L)
                .nickname("test1")
                .email("test@gmail.com")
                .isKeywordAvail(true)
                .isNoticeAvail(true)
                .profileImgUrl("https://shareeat-github-actions-s3-bucket.s3.ap-northeast-2.amazonaws.com/images/c97250ae-0b71-4983-a131-56f0c3bd57b4_unnamed.jpg")
                .locationPoint(memberLocation)
                .addressSt("addressSt")
                .addressDetail("addressDetail")
                .provider(provider)
                .build();
    }

    /* Point 객체 생성 */
    private Point generatePoint(){
        Point memberLocation =new GeometryFactory().createPoint(
                new Coordinate(127.099492,36.798331)
        );
        memberLocation.setSRID(4326);
        return memberLocation;
    }

    /* mock SharingPost 생성 */
    private SharingPost generateSharingPost(PostType postType,
                                            Point point,
                                            Member member) {

        return SharingPost.builder()
                .title("나눔글 제목 1")
                .category(PostCategory.BAKERY)
                .isFinished(true)
                .foodName("음식 이름 1")
                .expDate(LocalDate.now())
                .purchaseDate(LocalDate.now())
                .addressSt("장소 1")
                .addressDetail("상세 장소 1")
                .kakaoLocationCode("1111111")
                .locationPoint(point)
                .description("상세 설명")
                .postType(postType)
                .status(PostStatus.AVAILABLE)
                .writer(member)
                .noticed(false)
                .endAt(LocalDateTime.now())
                .build();
    }

    /* mapRequestDto 생성 */
    private MapRequestDto generateMapRequestDto(Double longitude, Double latitude){
        return new MapRequestDto(longitude,latitude);
    }
}