package com.carpBread.shareEatIt.domain.auth.oauth2.entity;

import com.carpBread.shareEatIt.domain.auth.LoginProvider;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.global.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "OAUTH2_TOKEN")
@Getter @SuperBuilder
@NoArgsConstructor
public class OAuth2Token extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(value = EnumType.STRING)
    @NotNull
    private LoginProvider provider;

    @Column(name = "access_token")
    private String accessToken;

    public void updateAccessToken(String accessToken){
        this.accessToken=accessToken;
    }

    public OAuth2Token(Member member, LoginProvider provider, String accessToken){
        super();
        this.member=member;
        this.provider=provider;
        this.accessToken=accessToken;

    }


}
