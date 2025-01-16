package com.carpBread.shareEatIt.domain.participation.service;

import com.carpBread.shareEatIt.config.WithMockCustomUser;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.participation.dto.GratitudeResponseDto;
import com.carpBread.shareEatIt.domain.participation.entity.GratitudeSticker;
import com.carpBread.shareEatIt.domain.participation.entity.GratitudeType;
import com.carpBread.shareEatIt.domain.participation.entity.Participation;
import com.carpBread.shareEatIt.domain.participation.entity.ParticipationStatus;
import com.carpBread.shareEatIt.domain.participation.repository.GratitudeStickerRepository;
import com.carpBread.shareEatIt.domain.participation.repository.ParticipationRepository;
import com.carpBread.shareEatIt.domain.sharingPost.entity.PostStatus;
import com.carpBread.shareEatIt.domain.sharingPost.entity.PostType;
import com.carpBread.shareEatIt.domain.sharingPost.entity.SharingPost;
import com.carpBread.shareEatIt.domain.sharingPost.repository.SharingPostRepository;
import com.carpBread.shareEatIt.global.exception.AppException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.carpBread.shareEatIt.domain.member.entity.Provider.INDIVIDUAL;
import static com.carpBread.shareEatIt.domain.sharingPost.entity.PostCategory.BAKERY;
import static com.carpBread.shareEatIt.global.exception.ErrorCode.ALREADY_EXISTS_GRATITUDESTICKER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)  // 테스트 메서드명의 언더 스코어를 공백으로 대체
class GratitudeStickerServiceTest {

    @Mock
    private SharingPostRepository sharingPostRepository;
    @Mock
    private ParticipationRepository participationRepository;
    @Mock
    private GratitudeStickerRepository gratitudeStickerRepository;
    @InjectMocks
    private GratitudeStickerService gratitudeStickerService;

    private Member mockMemberGiver;
    private Member mockMemberReceiver;
    private SharingPost mockPost;
    private Participation mockParticipation;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        GeometryFactory geometryFactory = new GeometryFactory();
        Point mockLocationPoint = geometryFactory.createPoint(new Coordinate(127.001698, 37.564213)); // 가상의 좌표 생성

        // 가짜 멤버 객체1
        mockMemberGiver = Member.builder()
                .id(1L)
                .email("giver@example.com")
                .nickname("GiverUser")
                .accessToken("mockAccessTokenGiver")
                .accessId(111111L)
                .refreshToken("mockRefreshTokenGiver")
                .profileImgUrl("https://example.com/mockProfileImgGiver.jpg")
                .isKeywordAvail(true)
                .isNoticeAvail(true)
                .addressSt("서울특별시 송파구")
                .addressDetail("테스트 빌딩 101호")
                .locationPoint(mockLocationPoint)
                .provider(INDIVIDUAL)
                .build();

        // 가짜 멤버 객체2
        mockMemberReceiver = Member.builder()
                .id(2L)
                .email("receiver@example.com")
                .nickname("ReceiverUser")
                .accessToken("mockAccessTokenReceiver")
                .accessId(222222L)
                .refreshToken("mockRefreshTokenReceiver")
                .profileImgUrl("https://example.com/mockProfileImgReceiver.jpg")
                .isKeywordAvail(false)
                .isNoticeAvail(false)
                .addressSt("서울특별시 강남구")
                .addressDetail("테스트 빌딩 202호")
                .locationPoint(mockLocationPoint)
                .provider(INDIVIDUAL)
                .build();

        // 가짜 게시글 객체
        mockPost = SharingPost.builder()
                .id(1L)
                .title("Mock Post Title")
                .category(BAKERY)
                .isFinished(true)
                .foodName("Mock Food Name")
                .expDate(null)
                .endAt(null)
                .purchaseDate(null)
                .addressSt("서울특별시 강남구")
                .addressDetail("테스트 빌딩 202호")
                .locationPoint(mockLocationPoint)
                .description("Mock description")
                .postType(PostType.INDIVIDUAL)
                .status(PostStatus.COMPLETED)
                .writer(mockMemberGiver)
                .build();

        // 가짜 참여 객체
        mockParticipation = Participation.builder()
                .id(1L)
                .post(mockPost)
                .status(ParticipationStatus.COMPLETED)
                .completedAt(LocalDateTime.now())
                .giver(mockMemberGiver)
                .receiver(mockMemberReceiver)
                .isGiverInChat(true)
                .isReceiverInChat(true)
                .build();

    }

    @Test
    @WithMockCustomUser
    public void 고마움스티커_생성_성공() {
        // given
        GratitudeType GRATITUDE_TYPE = GratitudeType.SMILE1;

        // stub 설정 - 독립적인 테스트 위한 의존성 제거
        Mockito.when(sharingPostRepository.findById(mockPost.getId()))
                .thenReturn(Optional.of(mockPost));
        Mockito.when(participationRepository.findByPostIdAndStatus(mockPost.getId()))
                .thenReturn(List.of(mockParticipation));
        Mockito.when(gratitudeStickerRepository.existsByParticipationId(mockParticipation.getId()))
                .thenReturn(false);
        // save() 메서드 호출 시 전달된 첫 번째 인자(GratitudeSticker 객체)를 그대로 반환하도록 설정
        // 실제 DB에 저장하지 않고도 save() 메서드 호출 결과를 흉내 내어 독립적인 단위 테스트 가능
        Mockito.when(gratitudeStickerRepository.save(any(GratitudeSticker.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        GratitudeResponseDto result = gratitudeStickerService.createGratitudeSticker(mockPost.getId(), mockMemberReceiver, GRATITUDE_TYPE);

        // then
        // 결과값 검증
        assertNotNull(result);
        assertEquals(GRATITUDE_TYPE, result.getGratitudeType()); // 요청값의 스티커타입과 실행 결과 생성된 스티커 타입의 일치 여부 검증
        assertEquals(mockPost.getId(), result.getSharingPostId()); // Post ID 검증
        assertEquals(mockMemberReceiver.getId(), result.getReviewerId()); // Reviewer ID 검증
        // 메서드 호출 검증
        verify(sharingPostRepository).findById(mockPost.getId());
        verify(participationRepository).findByPostIdAndStatus(mockPost.getId());
        verify(gratitudeStickerRepository).save(any(GratitudeSticker.class));

    }

    @Test
    @WithMockCustomUser
    public void 고마움스티커_생성_실패_이미_존재하는_스티커() {
        // given
        GratitudeType GRATITUDE_TYPE = GratitudeType.SMILE1;

        // stub 설정 - 독립적인 테스트 위한 의존성 제거
        Mockito.when(sharingPostRepository.findById(mockPost.getId()))
                .thenReturn(Optional.of(mockPost));
        Mockito.when(participationRepository.findByPostIdAndStatus(mockPost.getId()))
                .thenReturn(List.of(mockParticipation));
        Mockito.when(gratitudeStickerRepository.existsByParticipationId(mockParticipation.getId()))
                .thenReturn(true);  // 이미 고마움 스티커 존재하는 경우

        // when
        AppException exception = assertThrows(AppException.class,
                () -> gratitudeStickerService.createGratitudeSticker(mockPost.getId(), mockMemberReceiver, GratitudeType.SMILE1));

        // then
        // 예외 메시지 & 상태 코드 검증
        assertEquals(ALREADY_EXISTS_GRATITUDESTICKER, exception.getErrorCode());  // 에러 상태 코드 검증
        assertEquals("이미 고마움을 남긴 나눔입니다.", exception.getMessage());  // 에러 메시지 검증
        assertEquals("/gratitudeStickers/" + mockPost.getId(), exception.getPath());  // 에러 경로 표시 검증
        // 메서드 호출 검증
        verify(sharingPostRepository).findById(mockPost.getId());
        verify(participationRepository).findByPostIdAndStatus(mockPost.getId());
        verify(gratitudeStickerRepository).existsByParticipationId(mockParticipation.getId());

    }

    @Test
    void updateGratitudeStickers() {
        //given

        //when

        //then
    }
}