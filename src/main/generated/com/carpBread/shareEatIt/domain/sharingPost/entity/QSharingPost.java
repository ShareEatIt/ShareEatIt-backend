package com.carpBread.shareEatIt.domain.sharingPost.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSharingPost is a Querydsl query type for SharingPost
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSharingPost extends EntityPathBase<SharingPost> {

    private static final long serialVersionUID = 559118466L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSharingPost sharingPost = new QSharingPost("sharingPost");

    public final com.carpBread.shareEatIt.global.entity.QBaseEntity _super = new com.carpBread.shareEatIt.global.entity.QBaseEntity(this);

    public final StringPath addressDetail = createString("addressDetail");

    public final StringPath addressSt = createString("addressSt");

    public final EnumPath<PostCategory> category = createEnum("category", PostCategory.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath description = createString("description");

    public final DateTimePath<java.time.LocalDateTime> endAt = createDateTime("endAt", java.time.LocalDateTime.class);

    public final DatePath<java.time.LocalDate> expDate = createDate("expDate", java.time.LocalDate.class);

    public final StringPath foodName = createString("foodName");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isFinished = createBoolean("isFinished");

    public final StringPath kakaoLocationCode = createString("kakaoLocationCode");

    public final ComparablePath<org.locationtech.jts.geom.Point> locationPoint = createComparable("locationPoint", org.locationtech.jts.geom.Point.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final BooleanPath noticed = createBoolean("noticed");

    public final ListPath<com.carpBread.shareEatIt.domain.participation.entity.Participation, com.carpBread.shareEatIt.domain.participation.entity.QParticipation> participationList = this.<com.carpBread.shareEatIt.domain.participation.entity.Participation, com.carpBread.shareEatIt.domain.participation.entity.QParticipation>createList("participationList", com.carpBread.shareEatIt.domain.participation.entity.Participation.class, com.carpBread.shareEatIt.domain.participation.entity.QParticipation.class, PathInits.DIRECT2);

    public final ListPath<PostImgUrl, QPostImgUrl> postImgUrlList = this.<PostImgUrl, QPostImgUrl>createList("postImgUrlList", PostImgUrl.class, QPostImgUrl.class, PathInits.DIRECT2);

    public final EnumPath<PostType> postType = createEnum("postType", PostType.class);

    public final DatePath<java.time.LocalDate> purchaseDate = createDate("purchaseDate", java.time.LocalDate.class);

    public final EnumPath<PostStatus> status = createEnum("status", PostStatus.class);

    public final StringPath title = createString("title");

    public final com.carpBread.shareEatIt.domain.member.entity.QMember writer;

    public QSharingPost(String variable) {
        this(SharingPost.class, forVariable(variable), INITS);
    }

    public QSharingPost(Path<? extends SharingPost> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSharingPost(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSharingPost(PathMetadata metadata, PathInits inits) {
        this(SharingPost.class, metadata, inits);
    }

    public QSharingPost(Class<? extends SharingPost> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.writer = inits.isInitialized("writer") ? new com.carpBread.shareEatIt.domain.member.entity.QMember(forProperty("writer")) : null;
    }

}

