package com.carpBread.shareEatIt.domain.sharingPost.service;

import com.carpBread.shareEatIt.config.GeometryFactoryConfig;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.member.entity.Provider;
import com.carpBread.shareEatIt.domain.participation.repository.GratitudeStickerRepository;
import com.carpBread.shareEatIt.domain.sharingPost.dto.SharingPostListRequestDto;
import com.carpBread.shareEatIt.domain.sharingPost.dto.SharingPostListResponseDto;
import com.carpBread.shareEatIt.domain.sharingPost.dto.SharingPostResponseDto;
import com.carpBread.shareEatIt.domain.sharingPost.entity.*;
import com.carpBread.shareEatIt.domain.sharingPost.repository.PostImgUrlRepository;
import com.carpBread.shareEatIt.domain.sharingPost.repository.SharingPostQuerydslRepository;
import com.carpBread.shareEatIt.domain.sharingPost.repository.SharingPostRepository;
import com.carpBread.shareEatIt.global.config.QuerydslConfig;
import com.carpBread.shareEatIt.global.exception.CustomException;
import com.carpBread.shareEatIt.global.exception.CustomExceptionStatus;
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
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/* SharingPostReadService 단위 테스트 */
@ExtendWith(MockitoExtension.class)
@Import({GeometryFactoryConfig.class, QuerydslConfig.class})
class SharingPostReadServiceTest {

    @InjectMocks
    private SharingPostReadService sharingPostReadService;

    @Mock
    private SharingPostRepository sharingPostRepository;

    @Mock
    private PostImgUrlRepository postImgUrlRepository;

    @Mock
    private SharingPostQuerydslRepository sharingPostQuerydslRepository;

    @Mock
    private GratitudeStickerRepository gratitudeStickerRepository;

    @Test
    @DisplayName("성공 : provider 타입과 위도/경도로 sharing post 리스트 조회 성공 단위 테스트")
    void findPostListByProviderTypeSuccessTest() {
        // given

        // 1. sharingPostListRequestDto 생성
        SharingPostListRequestDto requestDto = generateRequestDto("INDIVIDUAL");

        // 2. testMember 객체 생성
        Member testMember = createTestMember(Provider.INDIVIDUAL);

        // 2. sharingPostRepository.findSharingPostsByPostTypeWithinRadius RETURN 값 지정
        Point point = generatePoint();
        SharingPost post = generateSharingPost(PostType.INDIVIDUAL, point, testMember);
        ArrayList<SharingPost> postList = new ArrayList<>();
        postList.add(post);
        Mockito.doReturn(postList).when(sharingPostQuerydslRepository)
                .findSharingPostsByPostTypeWithinRadius(Mockito.anyDouble(),Mockito.anyDouble(),
                        Mockito.anyDouble(), Mockito.any());

        // 3. postImgUrlRepository save return 값 지정
        List<PostImgUrl> postImgUrls = generatePostImgUrlList(post);
        Mockito.doReturn(postImgUrls).when(postImgUrlRepository)
                .findByPost(Mockito.any());

        // when
        SharingPostListResponseDto responseDto = sharingPostReadService.findPostListByProviderType(requestDto);

        // then
        assertThat(responseDto.getPostList().get(0).getCategory()).isEqualTo(post.getCategory().name());

    }

    @Test
    @DisplayName("실패 : 잘못된 provider 타입으로 sharing post 리스트 조회 시 실패 단위 테스트")
    void findPostListByProviderTypeFailTest() {
        // given

        // 1. sharingPostListRequestDto 생성
        SharingPostListRequestDto requestDto = generateRequestDto("INDIVIDUL");

        // 2. testMember 객체 생성
        Member testMember = createTestMember(Provider.INDIVIDUAL);

        // when & then
        CustomException thrownException = assertThrows(CustomException.class, () ->
                sharingPostReadService.findPostListByProviderType(requestDto));
//        assertThat(thrownException.getCustomExceptionStatus()).isEqualTo(CustomExceptionStatus.INVALID_ENUM_VALUE);

    }

    @Test
    @DisplayName("성공 : 나눔글 id로 나눔글 조회 성공 단위테스트")
    void findSharingPostByIDSuccessTest() {
        // given
        // 1. testmember 객체 생성
        Member testMember = createTestMember(Provider.INDIVIDUAL);

        // 2. sharingPostRepository.findById 리턴 값 지정
        Point point = generatePoint();
        SharingPost post = generateSharingPost(PostType.INDIVIDUAL, point, testMember);
        Optional<SharingPost> optionalSharingPost = Optional.of(post);

        Mockito.doReturn(optionalSharingPost).when(sharingPostRepository)
                .findById(Mockito.any());

        // 3. sharingPostRepository countByWriter 리턴 값 지정
        Mockito.doReturn(2L).when(sharingPostRepository)
                .countByWriter(Mockito.any());

        // 4. gratitudeStickerRepository.existsByPost return 값 지정
        Mockito.doReturn(false).when(gratitudeStickerRepository)
                .existsByPost(Mockito.any());

        // when
        SharingPostResponseDto responseDto = sharingPostReadService.findSharingPostByID(testMember, 1L);

        // then
        assertThat(responseDto.getSubject()).isEqualTo("WRITER");
        assertThat(responseDto.getFoodName()).isEqualTo(post.getFoodName());
    }

    /* sharingPostListRequestDto 생성 */
    private SharingPostListRequestDto generateRequestDto(String postType){
        return new SharingPostListRequestDto(postType,37.5642135,127.0016985);
    }

    /* mock SharingPost 생성 */
    private SharingPost generateSharingPost(PostType postType,
                                            Point point,
                                            Member member) {

        return SharingPost.builder()
                .id(1L)
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
                .createdAt(LocalDateTime.now())
                .endAt(LocalDateTime.now())
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

    /* postImgUrl 객체 생성 */
    private List<PostImgUrl> generatePostImgUrlList(SharingPost post){
        ArrayList<PostImgUrl> imgList = new ArrayList<>();
        PostImgUrl postImgUrl = PostImgUrl.builder()
                .url("url")
                .imgOrder(1)
                .post(post)
                .build();
        imgList.add(postImgUrl);
        return imgList;
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
}