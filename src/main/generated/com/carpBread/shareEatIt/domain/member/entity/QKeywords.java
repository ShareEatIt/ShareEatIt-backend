package com.carpBread.shareEatIt.domain.member.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QKeywords is a Querydsl query type for Keywords
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QKeywords extends EntityPathBase<Keywords> {

    private static final long serialVersionUID = 713180192L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QKeywords keywords = new QKeywords("keywords");

    public final BooleanPath avail = createBoolean("avail");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath keyword = createString("keyword");

    public final QMember member;

    public QKeywords(String variable) {
        this(Keywords.class, forVariable(variable), INITS);
    }

    public QKeywords(Path<? extends Keywords> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QKeywords(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QKeywords(PathMetadata metadata, PathInits inits) {
        this(Keywords.class, metadata, inits);
    }

    public QKeywords(Class<? extends Keywords> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member")) : null;
    }

}

