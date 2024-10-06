package com.carpBread.shareEatIt.domain.participation.entity;

import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.sharingPost.entity.SharingPost;
import com.carpBread.shareEatIt.global.entity.BaseEntity;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


import java.time.LocalDateTime;

@Entity
@Table(name = "PARTICIPATIONS")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
public class Participation extends BaseEntity {

    @Id
    @Column(name = "pt_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "post_id")
    @NotNull
    private SharingPost post;

    @Enumerated(value = EnumType.STRING)
    @NotNull
    private ParticipationStatus status;

    @Column(name = "completed_at")
    @Nullable
    private LocalDateTime completedAt;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "giver_id")
    @NotNull
    private Member giver;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "receiver_id")
    @NotNull
    private Member receiver;

    @Column(name = "is_giver_in_chat")
    @NotNull
    private Boolean isGiverInChat;

    @Column(name = "is_receiver_in_chat")
    @NotNull
    private Boolean isReceiverInChat;

}
