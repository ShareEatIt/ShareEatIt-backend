package com.carpBread.shareEatIt.domain.auth.oauth2.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QOAuth2Token is a Querydsl query type for OAuth2Token
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOAuth2Token extends EntityPathBase<OAuth2Token> {

    private static final long serialVersionUID = 1429533503L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QOAuth2Token oAuth2Token = new QOAuth2Token("oAuth2Token");

    public final com.carpBread.shareEatIt.global.entity.QBaseEntity _super = new com.carpBread.shareEatIt.global.entity.QBaseEntity(this);

    public final StringPath accessToken = createString("accessToken");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.carpBread.shareEatIt.domain.member.entity.QMember member;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final EnumPath<com.carpBread.shareEatIt.domain.auth.LoginProvider> provider = createEnum("provider", com.carpBread.shareEatIt.domain.auth.LoginProvider.class);

    public QOAuth2Token(String variable) {
        this(OAuth2Token.class, forVariable(variable), INITS);
    }

    public QOAuth2Token(Path<? extends OAuth2Token> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QOAuth2Token(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QOAuth2Token(PathMetadata metadata, PathInits inits) {
        this(OAuth2Token.class, metadata, inits);
    }

    public QOAuth2Token(Class<? extends OAuth2Token> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new com.carpBread.shareEatIt.domain.member.entity.QMember(forProperty("member")) : null;
    }

}

