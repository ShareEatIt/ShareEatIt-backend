package com.carpBread.shareEatIt.domain.report.entity;


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

import java.time.LocalDateTime;

@Entity
@Table(name = "REPORT")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
public class Report extends BaseEntity {

    @Id
    @Column(name = "report_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(value = EnumType.STRING)
    private ReportStatus status;

    @Column(columnDefinition = "TEXT")
    @Nullable
    private String response;

    private String imgUrl;

    @Column(name = "reviewed_at")
    @Nullable
    private LocalDateTime reviewedAt;

    @Column(name = "response_at")
    @Nullable
    private LocalDateTime responseAt;

    @ManyToOne
    @JoinColumn(name = "reporter_id")
    private Member reporter;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private SharingPost post;

}
