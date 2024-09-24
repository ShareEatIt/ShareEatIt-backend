package com.carpBread.shareEatIt.domain.participation.entity;

import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.sharingPost.entity.SharingPost;
import com.carpBread.shareEatIt.global.entity.BaseEntity;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "REVIEW")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
public class Review extends BaseEntity {

    @Id
    @Column(name = "review_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    private Reaction reaction;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "giver_id")
    private Member giver;

    @ManyToOne
    @JoinColumn(name = "reviewer_id")
    @Nullable
    private Member reviewer;

    @OneToOne
    @JoinColumn(name = "pt_id")
    private Participation participation;

    @OneToOne
    @JoinColumn(name = "sp_id")
    private SharingPost post;


}
