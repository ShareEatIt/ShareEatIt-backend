package com.carpBread.shareEatIt.domain.participation.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QGratitudeSticker is a Querydsl query type for GratitudeSticker
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QGratitudeSticker extends EntityPathBase<GratitudeSticker> {

    private static final long serialVersionUID = -1347164497L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QGratitudeSticker gratitudeSticker = new QGratitudeSticker("gratitudeSticker");

    public final com.carpBread.shareEatIt.global.entity.QBaseEntity _super = new com.carpBread.shareEatIt.global.entity.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final com.carpBread.shareEatIt.domain.member.entity.QMember giver;

    public final EnumPath<GratitudeType> gratitudeType = createEnum("gratitudeType", GratitudeType.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final QParticipation participation;

    public final com.carpBread.shareEatIt.domain.sharingPost.entity.QSharingPost post;

    public final com.carpBread.shareEatIt.domain.member.entity.QMember reviewer;

    public QGratitudeSticker(String variable) {
        this(GratitudeSticker.class, forVariable(variable), INITS);
    }

    public QGratitudeSticker(Path<? extends GratitudeSticker> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QGratitudeSticker(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QGratitudeSticker(PathMetadata metadata, PathInits inits) {
        this(GratitudeSticker.class, metadata, inits);
    }

    public QGratitudeSticker(Class<? extends GratitudeSticker> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.giver = inits.isInitialized("giver") ? new com.carpBread.shareEatIt.domain.member.entity.QMember(forProperty("giver")) : null;
        this.participation = inits.isInitialized("participation") ? new QParticipation(forProperty("participation"), inits.get("participation")) : null;
        this.post = inits.isInitialized("post") ? new com.carpBread.shareEatIt.domain.sharingPost.entity.QSharingPost(forProperty("post"), inits.get("post")) : null;
        this.reviewer = inits.isInitialized("reviewer") ? new com.carpBread.shareEatIt.domain.member.entity.QMember(forProperty("reviewer")) : null;
    }

}

