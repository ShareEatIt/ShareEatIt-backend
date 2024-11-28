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
import org.locationtech.jts.geom.Point;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "MEMBER")
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
public class Member extends BaseEntity {

    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String email;

    @NotNull
    private String nickname;

    @Column(name = "kakao_access_token")
    private String accessToken;

    @Column(name = "kakao_access_id")
    private Long accessId;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "profile_img_url")
    @Nullable
    private String profileImgUrl;

    @Column(name = "keyword_avail")
    @NotNull
    private Boolean isKeywordAvail;

    @Column(name = "notice_avail")
    @NotNull
    private Boolean isNoticeAvail;

    @Column(name = "address_st")
    private String addressSt;

    @Column(name = "address_detail")
    private String addressDetail;

    @Column(columnDefinition = "POINT", name = "location_point")
    private Point locationPoint;

    @Enumerated(value = EnumType.STRING)
    @NotNull
    private Provider provider;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    List<Keywords> keywordsList = new ArrayList<>();

    public void updateAccessToken(String accessToken){
        this.accessToken=accessToken;
    }

    public void updateMemberProfile(MemberProfileUpdateRequestDto dto,Point point, String imgUrl){
        this.nickname=dto.getNickname();
        this.locationPoint=point;
        this.profileImgUrl=imgUrl;
        this.addressSt=dto.getAddressSt();
        this.addressDetail=dto.getAddressDetail();
        this.provider=Provider.toEnum(dto.getProvider());
    }

    public void updateImgUrl(String url){
        this.profileImgUrl=url;

    }

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
