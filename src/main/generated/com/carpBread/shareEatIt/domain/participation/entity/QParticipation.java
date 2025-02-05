package com.carpBread.shareEatIt.domain.participation.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QParticipation is a Querydsl query type for Participation
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QParticipation extends EntityPathBase<Participation> {

    private static final long serialVersionUID = -1409782686L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QParticipation participation = new QParticipation("participation");

    public final com.carpBread.shareEatIt.global.entity.QBaseEntity _super = new com.carpBread.shareEatIt.global.entity.QBaseEntity(this);

    public final DateTimePath<java.time.LocalDateTime> completedAt = createDateTime("completedAt", java.time.LocalDateTime.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final com.carpBread.shareEatIt.domain.member.entity.QMember giver;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isGiverInChat = createBoolean("isGiverInChat");

    public final BooleanPath isReceiverInChat = createBoolean("isReceiverInChat");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final com.carpBread.shareEatIt.domain.sharingPost.entity.QSharingPost post;

    public final com.carpBread.shareEatIt.domain.member.entity.QMember receiver;

    public final EnumPath<ParticipationStatus> status = createEnum("status", ParticipationStatus.class);

    public QParticipation(String variable) {
        this(Participation.class, forVariable(variable), INITS);
    }

    public QParticipation(Path<? extends Participation> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QParticipation(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QParticipation(PathMetadata metadata, PathInits inits) {
        this(Participation.class, metadata, inits);
    }

    public QParticipation(Class<? extends Participation> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.giver = inits.isInitialized("giver") ? new com.carpBread.shareEatIt.domain.member.entity.QMember(forProperty("giver")) : null;
        this.post = inits.isInitialized("post") ? new com.carpBread.shareEatIt.domain.sharingPost.entity.QSharingPost(forProperty("post"), inits.get("post")) : null;
        this.receiver = inits.isInitialized("receiver") ? new com.carpBread.shareEatIt.domain.member.entity.QMember(forProperty("receiver")) : null;
    }

}

