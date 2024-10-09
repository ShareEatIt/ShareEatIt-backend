package com.carpBread.shareEatIt.domain.participation.entity;

import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.sharingPost.entity.SharingPost;
import com.carpBread.shareEatIt.global.entity.BaseEntity;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "GRATITUDE_STICKERS")
@NoArgsConstructor
@SuperBuilder
@Getter
public class GratitudeSticker extends BaseEntity {

    @Id
    @Column(name = "gs_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "post_id")
    @NotNull
    private SharingPost post;

    @OneToOne
    @JoinColumn(name = "pt_id")
    @NotNull
    private Participation participation;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "giver_id")
    @NotNull
    private Member giver;

    @ManyToOne
    @JoinColumn(name = "reviewer_id")
    @Nullable   // 참여자가 탈퇴한 경우에도 고마움기록이 남아있도록
    private Member reviewer;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "gratitude_type")
    @NotNull
    private GratitudeType gratitudeType;

}
