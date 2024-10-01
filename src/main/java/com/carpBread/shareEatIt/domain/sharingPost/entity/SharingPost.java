package com.carpBread.shareEatIt.domain.sharingPost.entity;

import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.global.entity.BaseEntity;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.sql.Date;

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
    private Date expDate;

    @Column(name = "end_date")
    private Date endDate;

    @Column(name = "purchase_date")
    @Nullable
    private Date purchaseDate;

    @Column(name = "address_st")
    private String addressSt;

    @Column(name = "address_detail")
    private String addressDetail;

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

}
