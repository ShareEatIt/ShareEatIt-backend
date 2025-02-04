package com.carpBread.shareEatIt.domain.report.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QReport is a Querydsl query type for Report
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReport extends EntityPathBase<Report> {

    private static final long serialVersionUID = 1588122692L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QReport report = new QReport("report");

    public final com.carpBread.shareEatIt.global.entity.QBaseEntity _super = new com.carpBread.shareEatIt.global.entity.QBaseEntity(this);

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath imgUrl = createString("imgUrl");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final com.carpBread.shareEatIt.domain.sharingPost.entity.QSharingPost post;

    public final com.carpBread.shareEatIt.domain.member.entity.QMember reporter;

    public final StringPath response = createString("response");

    public final DateTimePath<java.time.LocalDateTime> responseAt = createDateTime("responseAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> reviewedAt = createDateTime("reviewedAt", java.time.LocalDateTime.class);

    public final EnumPath<ReportStatus> status = createEnum("status", ReportStatus.class);

    public final StringPath title = createString("title");

    public QReport(String variable) {
        this(Report.class, forVariable(variable), INITS);
    }

    public QReport(Path<? extends Report> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QReport(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QReport(PathMetadata metadata, PathInits inits) {
        this(Report.class, metadata, inits);
    }

    public QReport(Class<? extends Report> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.post = inits.isInitialized("post") ? new com.carpBread.shareEatIt.domain.sharingPost.entity.QSharingPost(forProperty("post"), inits.get("post")) : null;
        this.reporter = inits.isInitialized("reporter") ? new com.carpBread.shareEatIt.domain.member.entity.QMember(forProperty("reporter")) : null;
    }

}

