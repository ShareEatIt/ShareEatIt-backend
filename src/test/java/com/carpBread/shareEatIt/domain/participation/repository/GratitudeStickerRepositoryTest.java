package com.carpBread.shareEatIt.domain.participation.repository;

import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.member.repository.MemberRepository;
import com.carpBread.shareEatIt.domain.participation.entity.GratitudeSticker;
import com.carpBread.shareEatIt.domain.participation.entity.GratitudeType;
import com.carpBread.shareEatIt.domain.participation.entity.Participation;
import com.carpBread.shareEatIt.domain.participation.entity.ParticipationStatus;
import com.carpBread.shareEatIt.domain.sharingPost.entity.PostStatus;
import com.carpBread.shareEatIt.domain.sharingPost.entity.PostType;
import com.carpBread.shareEatIt.domain.sharingPost.entity.SharingPost;
import com.carpBread.shareEatIt.domain.sharingPost.repository.SharingPostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;

import static com.carpBread.shareEatIt.domain.member.entity.Provider.INDIVIDUAL;
import static com.carpBread.shareEatIt.domain.sharingPost.entity.PostCategory.BAKERY;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class GratitudeStickerRepositoryTest {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private GratitudeStickerRepository gratitudeStickerRepository;
    @Autowired
    private SharingPostRepository sharingPostRepository;
    @Autowired
    private ParticipationRepository participationRepository;

    // 미리 정의해둔 객체
    private Member mockMemberGiver;
    private Member mockMemberReceiver;
    private SharingPost mockPost;
    private Participation mockParticipation;
    private GratitudeSticker mockGratitudeSticker;

    // DB에 저장된 객체
    private Member savedMockMemberGiver;
    private Member savedMockMemberReceiver;
    private SharingPost savedMockPost;
    private Participation savedMockParticipation;
    private GratitudeSticker savedMockGratitudeSticker;

    @BeforeEach
    void setUp() {
        GeometryFactory geometryFactory = new GeometryFactory();
        Point mockLocationPoint = geometryFactory.createPoint(new Coordinate(127.001698, 37.564213)); // 가상의 좌표 생성

        // 가짜 멤버 객체1
        mockMemberGiver = Member.builder()
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
                .post(mockPost)
                .status(ParticipationStatus.COMPLETED)
                .completedAt(LocalDateTime.now())
                .giver(mockMemberGiver)
                .receiver(mockMemberReceiver)
                .isGiverInChat(true)
                .isReceiverInChat(true)
                .build();

        // 가짜 스티커 객체
        mockGratitudeSticker = GratitudeSticker.builder()
                .post(mockPost)
                .participation(mockParticipation)
                .giver(mockMemberGiver)
                .reviewer(mockMemberReceiver)
                .gratitudeType(GratitudeType.SMILE1)
                .build();

        // DB에 저장
        savedMockMemberGiver = memberRepository.save(mockMemberGiver);   // mockMemberGiver 저장
        savedMockMemberReceiver = memberRepository.save(mockMemberReceiver);  // mockMemberReceiver 저장
        savedMockPost = sharingPostRepository.save(mockPost);  // mockPost 저장
        savedMockParticipation = participationRepository.save(mockParticipation); // mockParticipation 저장
        savedMockGratitudeSticker = gratitudeStickerRepository.save(mockGratitudeSticker); // mockGratitudeSticker 저장
    }

    @Test
    @DisplayName("참여ID로 해당 참여 객체의 고마움스티커가 존재하는 경우 true 반환 검증")
    void existsByParticipationIdTest() {
        //given
        Long PT_ID = savedMockParticipation.getId();

        //when
        Boolean result = gratitudeStickerRepository.existsByParticipationId(PT_ID);

        //then
        assertTrue(result);
    }

    @Test
    @DisplayName("해당 나눔글 객체의 고마움스티커가 존재하는 경우 true 반환 검증")
    void existsByPostTest() {
        // when
        Boolean result = gratitudeStickerRepository.existsByPost(mockPost);

        // then
        assertTrue(result);
    }

    @Test
    @DisplayName("나눔글 객체로 해당 나눔글의 스티커가 있는 경우 스티커 객체 반환 검증")
    void findByPostTest() {
        //given
        Long gsId = savedMockGratitudeSticker.getId();
        GratitudeType gsType = savedMockGratitudeSticker.getGratitudeType();

        // when
        GratitudeSticker result = gratitudeStickerRepository.findByPost(mockPost).orElse(null);

        // then
        assertNotNull(result);
        assertEquals(gsId, result.getId());
        assertEquals(gsType, result.getGratitudeType());
    }

    @Test
    @DisplayName("멤버ID로 해당 멤버가 쓴 나눔글들의 총 고마움 스티커 개수 반환 검증")
    void countByGratitudeTypeByGiverTest() {
        //given
        Long giverId = savedMockMemberGiver.getId();
        int count = 0;

        // when
        List<Object[]> gratitudeStickers = gratitudeStickerRepository.countByGratitudeTypeByGiver(giverId);
        count = gratitudeStickers.size();

        // then
        assertEquals(1, count);
    }
}