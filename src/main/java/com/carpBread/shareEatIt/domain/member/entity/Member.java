package com.carpBread.shareEatIt.domain.member.entity;

import com.carpBread.shareEatIt.domain.member.dto.request.MemberProfileUpdateRequestDto;
import com.carpBread.shareEatIt.global.entity.BaseEntity;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.UniqueElements;
import org.locationtech.jts.geom.Point;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "MEMBER")
@NoArgsConstructor
@SuperBuilder
@Getter
public class Member extends BaseEntity {

    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    // 어플리케이션 자체 로그인 username
    private String username;

    // 어플리케이션 자체 로그인 password
    private String password;

    @NotNull
    private String nickname;

    private String accessToken;
    private Long accessId;

    // 회원 고유 refreshToken 값
    @Column(name = "refresh_token")
    private String refreshToken;

    // 회원 프로필 사진
    @Column(name = "profile_img_url")
    @Nullable
    private String profileImgUrl;

    // keyword 사용 여부
    @Column(name = "keyword_avail")
    @NotNull
    private Boolean isKeywordAvail;

    // 알람 받기 여부
    @Column(name = "notice_avail")
    @NotNull
    private Boolean isNoticeAvail;

    // 주소
    @Column(name = "address_st")
    private String addressSt;

    // 상세 주소
    @Column(name = "address_detail")
    private String addressDetail;

    // 회원 거주 위도/경도 위치
    @Column(columnDefinition = "POINT", name = "location_point")
    @NotNull
    private Point locationPoint;

    // 가게/개인 속성
    @Enumerated(value = EnumType.STRING)
    @NotNull
    private Provider provider;

    @PrePersist
    public void prePersist(){
        if (locationPoint!=null){
            locationPoint.setSRID(4326);
        }
    }

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    List<Keywords> keywordsList = new ArrayList<>();

    public void updateMemberProfile(MemberProfileUpdateRequestDto dto,Point point, String imgUrl){
        this.nickname=dto.getNickname();
        this.locationPoint=point;
        this.profileImgUrl=imgUrl;
        this.addressSt=dto.getAddressSt();
        this.addressDetail=dto.getAddressDetail();
        this.provider=Provider.toEnum(dto.getProvider());
    }


    public void updateAccessToken(String accessToken){this.accessToken=accessToken;}
    public void updateLocationPoint(Point point){
        this.locationPoint=point;
    }

    public void updateAvailKeyword(Boolean isKeywordAvail){
        this.isKeywordAvail=isKeywordAvail;
    }

    public void updateAvailNotice(Boolean isNoticeAvail){
        this.isNoticeAvail=isNoticeAvail;
    }

    public void updateRefreshToken(String newRefreshToken) {

        this.refreshToken=newRefreshToken;

    }
}
