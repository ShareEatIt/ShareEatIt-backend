package com.carpBread.shareEatIt.domain.sharingPost.entity;

import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.participation.entity.Participation;
import com.carpBread.shareEatIt.domain.participation.entity.ParticipationStatus;
import com.carpBread.shareEatIt.domain.sharingPost.dto.SharingPostRequestDto;
import com.carpBread.shareEatIt.domain.sharingPost.dto.SharingPostUpdateRequestDto;
import com.carpBread.shareEatIt.global.entity.BaseEntity;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;
import org.locationtech.jts.geom.Point;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "SHARING_POSTS")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
public class SharingPost extends BaseEntity {

    @Id
    @Column(name = "sp_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 500)
    @Nullable
    private String title;

    @Enumerated(value = EnumType.STRING)
    private PostCategory category;

    @Column(name = "is_finished")
    private Boolean isFinished;

    @Column(name = "food_name")
    private String foodName;

    @Column(name = "exp_date")
    private LocalDate expDate;

    @Column(name = "end_at")
    private LocalDateTime endAt;

    @Column(name = "purchase_date")
    @Nullable
    private LocalDate purchaseDate;

    @Column(name = "address_st")
    private String addressSt;

    @Column(name = "address_detail")
    private String addressDetail;

    @Column(columnDefinition = "POINT", name = "location_point")
    private Point locationPoint;

    private String kakaoLocationCode;

    @Column(length = 500)
    private String description;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "post_type")
    private PostType postType;

    @Enumerated(value = EnumType.STRING)
    private PostStatus status;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "writer_id")
    private Member writer;

    @OneToMany(mappedBy = "post",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    List<PostImgUrl> postImgUrlList=new ArrayList<>();

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    List<Participation> participationList = new ArrayList<>();

    // 참여 상태 변경
    public void updateStatus(PostStatus postStatus) {
        this.status = postStatus;
    }

    // 게시물 내용 변경
    public void updatePost(SharingPostUpdateRequestDto dto, Point point){
        this.title=dto.getTitle();
        this.category=PostCategory.toEnumType(dto.getCategory());
        this.isFinished=dto.getIsFinished();
        this.foodName= dto.getFoodName();
        this.expDate=dto.getExpDate();
        this.locationPoint=point;
        this.purchaseDate=dto.getPurchaseDate();
        this.addressSt= dto.getAddressSt();
        this.addressDetail= dto.getAddressDetail();
        this.kakaoLocationCode= dto.getKakaoLocationCode();
        this.description=dto.getDescription();
        this.endAt=dto.getEndAt();

    }

}
