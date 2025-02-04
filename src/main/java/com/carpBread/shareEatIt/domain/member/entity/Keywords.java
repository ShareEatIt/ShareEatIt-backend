package com.carpBread.shareEatIt.domain.member.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "keywords")
@NoArgsConstructor
@AllArgsConstructor
@Builder @Getter
public class Keywords {

    @Id
    @Column(name = "keywords_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50)
    private String keyword;

    private Boolean avail;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    public void changeAvail(Boolean avail){
        this.avail=avail;
    }
}
