package com.carpBread.shareEatIt.domain.sharingPost.repository;

import com.carpBread.shareEatIt.config.QuerydslTestConfig;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.member.entity.Provider;
import com.carpBread.shareEatIt.domain.member.repository.MemberRepository;
import com.carpBread.shareEatIt.domain.sharingPost.entity.PostCategory;
import com.carpBread.shareEatIt.domain.sharingPost.entity.PostStatus;
import com.carpBread.shareEatIt.domain.sharingPost.entity.PostType;
import com.carpBread.shareEatIt.domain.sharingPost.entity.SharingPost;
import com.querydsl.core.Tuple;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Transactional
@Import({QuerydslTestConfig.class,GeometryFactory.class})
class SharingPostQuerydslRepositoryImplTest {

    @Autowired
    private SharingPostRepository sharingPostRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private GeometryFactory geometryFactory;

    @Autowired
    private SharingPostQuerydslRepository sharingPostQuerydslRepository;

    @Test
    void findSharingPostsWithinRadiusSuccessTest() {
        // given
        Point memberLocation =geometryFactory.createPoint(
                new Coordinate(127.099491,36.798330)
        );
        memberLocation.setSRID(4326);

        Member member = Member.builder()
                .nickname("test1")
                .email("test@gmail.com")
                .isKeywordAvail(true)
                .isNoticeAvail(true)
                .locationPoint(memberLocation)
                .addressSt("충청남도 천안시 서북구 불당동")
                .addressDetail("공원로 176 303동")
                .provider(Provider.STORE)
                .build();
        Member savedMember = memberRepository.save(member);


        Point postLocation =geometryFactory.createPoint(
                new Coordinate(127.099738,36.794754)
        );
        postLocation.setSRID(4326);
        SharingPost newPost = SharingPost.builder()
                .title("제목")
                .category(PostCategory.BAKERY)
                .isFinished(true)
                .foodName("음식 이름")
                .expDate(LocalDate.now())
                .endAt(LocalDateTime.now())
                .locationPoint(postLocation)
                .purchaseDate(LocalDate.now())
                .addressSt("장소 1")
                .addressDetail("세부 주소 1")
                .kakaoLocationCode("1111")
                .description("설명")
                .postType(PostType.INDIVIDUAL)
                .status(PostStatus.AVAILABLE)
                .writer(savedMember)
                .noticed(false)
                .build();

        sharingPostRepository.save(newPost);

        // 1. parameter
        double latitude = 36.794754;
        double longitude = 127.099738;
        double radius = 100000.0;

        // when
        List<SharingPost> postList = sharingPostQuerydslRepository.findSharingPostsWithinRadius(latitude, longitude, radius);

        // then
        assertThat(postList.get(0).getId()).isEqualTo(newPost.getId());

    }

    @Test
    void findSharingPostsByPostTypeWithinRadiusSuccessTest() {
        // given
        Point memberLocation =geometryFactory.createPoint(
                new Coordinate(127.099491,36.798330)
        );
        memberLocation.setSRID(4326);

        Member member = Member.builder()
                .nickname("test1")
                .email("test@gmail.com")
                .isKeywordAvail(true)
                .isNoticeAvail(true)
                .locationPoint(memberLocation)
                .addressSt("충청남도 천안시 서북구 불당동")
                .addressDetail("공원로 176 303동")
                .provider(Provider.STORE)
                .build();
        Member savedMember = memberRepository.save(member);


        Point postLocation =geometryFactory.createPoint(
                new Coordinate(127.099738,36.794754)
        );
        postLocation.setSRID(4326);
        SharingPost newPost = SharingPost.builder()
                .title("제목")
                .category(PostCategory.BAKERY)
                .isFinished(true)
                .foodName("음식 이름")
                .expDate(LocalDate.now())
                .endAt(LocalDateTime.now())
                .locationPoint(postLocation)
                .purchaseDate(LocalDate.now())
                .addressSt("장소 1")
                .addressDetail("세부 주소 1")
                .kakaoLocationCode("1111")
                .description("설명")
                .postType(PostType.INDIVIDUAL)
                .status(PostStatus.AVAILABLE)
                .writer(savedMember)
                .noticed(false)
                .build();
        sharingPostRepository.save(newPost);

        // 1. parameter
        double latitude = 36.794754;
        double longitude = 127.099738;
        double radius = 100000.0;
        PostType postType=PostType.INDIVIDUAL;

        // when
        List<SharingPost> postList = sharingPostQuerydslRepository.findSharingPostsByPostTypeWithinRadius(latitude, longitude, radius, postType);

        // then
        assertThat(postList.get(0).getId()).isEqualTo(newPost.getId());

    }

    @Test
    void countByWriterSuccessTest() {
        // given
        Point memberLocation =geometryFactory.createPoint(
                new Coordinate(127.099491,36.798330)
        );
        memberLocation.setSRID(4326);

        Member member = Member.builder()
                .nickname("test1")
                .email("test@gmail.com")
                .isKeywordAvail(true)
                .isNoticeAvail(true)
                .locationPoint(memberLocation)
                .addressSt("충청남도 천안시 서북구 불당동")
                .addressDetail("공원로 176 303동")
                .provider(Provider.STORE)
                .build();
        Member savedMember = memberRepository.save(member);


        Point postLocation =geometryFactory.createPoint(
                new Coordinate(127.099738,36.794754)
        );
        postLocation.setSRID(4326);
        SharingPost newPost = SharingPost.builder()
                .title("제목")
                .category(PostCategory.BAKERY)
                .isFinished(true)
                .foodName("음식 이름")
                .expDate(LocalDate.now())
                .endAt(LocalDateTime.now())
                .locationPoint(postLocation)
                .purchaseDate(LocalDate.now())
                .addressSt("장소 1")
                .addressDetail("세부 주소 1")
                .kakaoLocationCode("1111")
                .description("설명")
                .postType(PostType.INDIVIDUAL)
                .status(PostStatus.AVAILABLE)
                .writer(savedMember)
                .noticed(false)
                .build();
        sharingPostRepository.save(newPost);

        Member testMember = Member.builder()
                .id(member.getId())
                .nickname("test1")
                .email("test@gmail.com")
                .isKeywordAvail(true)
                .isNoticeAvail(true)
                .provider(Provider.STORE)
                .build();

        // when
        Long count = sharingPostQuerydslRepository.countByWriter(testMember);

        // then
        assertThat(count).isNotEqualTo(0L);

    }

    @Test
    void countByCategoryForWriterSuccessTest() {

        // given
        Point memberLocation =geometryFactory.createPoint(
                new Coordinate(127.099491,36.798330)
        );
        memberLocation.setSRID(4326);

        Member member = Member.builder()
                .nickname("test1")
                .email("test@gmail.com")
                .isKeywordAvail(true)
                .isNoticeAvail(true)
                .locationPoint(memberLocation)
                .addressSt("충청남도 천안시 서북구 불당동")
                .addressDetail("공원로 176 303동")
                .provider(Provider.STORE)
                .build();
        Member savedMember = memberRepository.save(member);


        Point postLocation =geometryFactory.createPoint(
                new Coordinate(127.099738,36.794754)
        );
        postLocation.setSRID(4326);
        SharingPost newPost = SharingPost.builder()
                .title("제목")
                .category(PostCategory.BAKERY)
                .isFinished(true)
                .foodName("음식 이름")
                .expDate(LocalDate.now())
                .endAt(LocalDateTime.now())
                .locationPoint(postLocation)
                .purchaseDate(LocalDate.now())
                .addressSt("장소 1")
                .addressDetail("세부 주소 1")
                .kakaoLocationCode("1111")
                .description("설명")
                .postType(PostType.INDIVIDUAL)
                .status(PostStatus.AVAILABLE)
                .writer(savedMember)
                .noticed(false)
                .build();
        sharingPostRepository.save(newPost);

        Member testMember = Member.builder()
                .id(member.getId())
                .nickname("test1")
                .email("test@gmail.com")
                .isKeywordAvail(true)
                .isNoticeAvail(true)
                .provider(Provider.STORE)
                .build();

        // when
        List<Tuple> cntList = sharingPostQuerydslRepository.countByCategoryForWriter(testMember);

        // then
        Tuple obj= cntList.get(0);
        PostCategory category = (PostCategory) obj.get(0,PostCategory.class);
        Long count = (Long) obj.get(1,Long.class);
        assertThat(category).isEqualTo(PostCategory.BAKERY);
        assertThat(count).isNotEqualTo(0L);


    }
}