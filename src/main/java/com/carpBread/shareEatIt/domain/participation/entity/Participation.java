package com.carpBread.shareEatIt.domain.participation.entity;

import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.sharingPost.entity.SharingPost;
import com.carpBread.shareEatIt.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.print.attribute.standard.MediaSize;
import java.time.LocalDateTime;

@Entity
@Table(name = "PARTICIPATION")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
public class Participation extends BaseEntity {

    @Id
    @Column(name = "pt_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    private ParticipationStatus status;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "giver_id")
    private Member giver;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "post_id")
    private SharingPost post;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "review_id")
    private Review review;



}
