package com.carpBread.shareEatIt.global.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@NoArgsConstructor
@SuperBuilder @Getter
@EntityListeners(AuditingEntityListener.class) // JPA에서 엔티티의 생성 및 수정 시각을 자동으로 관리해주는 애노테이션
public class BaseEntity {

    @CreatedDate
    @Column(name = "created_at")
    @NotNull
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "modified_at")
    @Nullable
    private LocalDateTime modifiedAt;

}
