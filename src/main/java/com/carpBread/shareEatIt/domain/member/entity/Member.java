package com.carpBread.shareEatIt.domain.member.entity;

import com.carpBread.shareEatIt.domain.member.dto.MemberAvailRequestDto;
import com.carpBread.shareEatIt.domain.member.dto.MemberProfileResponseDto;
import com.carpBread.shareEatIt.domain.member.dto.MemberProfileUpdateRequestDto;
import com.carpBread.shareEatIt.global.entity.BaseEntity;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.print.attribute.standard.MediaSize;

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

    private String accessToken;
    private Long accessId;

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

    @Enumerated(value = EnumType.STRING)
    @NotNull
    private Provider provider;

    public void changeAccessToken(String accessToken){
        this.accessToken=accessToken;
    }

    public void changeMemberProfile(MemberProfileUpdateRequestDto dto){
        this.profileImgUrl=dto.getProfileImg();
        this.nickname=dto.getNickname();
        this.addressSt=dto.getAddressSt();
        this.addressDetail=dto.getAddressDetail();
    }

    public void changeAvail(MemberAvailRequestDto dto){
        this.isKeywordAvail=dto.getIsKeywordAvail();
        this.isNoticeAvail=dto.getIsNoticeAvail();
    }


}
