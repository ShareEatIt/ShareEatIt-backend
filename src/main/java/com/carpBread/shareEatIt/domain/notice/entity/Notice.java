package com.carpBread.shareEatIt.domain.notice.entity;

import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "NOTICE")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
public class Notice extends BaseEntity {
    @Id
    @Column(name = "notice_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    private NoticeType type;

    @Column(length = 100)
    private String title;

    @Column(length = 1000)
    private String message;

    @Column(name = "is_read")
    private Boolean isRead;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "member_id")
    private Member member;
}
