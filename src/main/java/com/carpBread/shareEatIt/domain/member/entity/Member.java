package com.carpBread.shareEatIt.domain.member.entity;

import com.carpBread.shareEatIt.global.entity.BaseEntity;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
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

    private String email;

    private String nickname;

    @Column(name = "profile_img_url")
    @Nullable
    private String profileImgUrl;

    @Column(name = "keyword_avail")
    private Boolean isKeywordAvail;

    @Column(name = "notice_avail")
    private Boolean isNoticeAvail;


    @Column(name = "address_st")
    private String addressSt;

    @Column(name = "address_detail")
    private String addressDetail;

    @Enumerated(value = EnumType.STRING)
    private Provider provider;



}
