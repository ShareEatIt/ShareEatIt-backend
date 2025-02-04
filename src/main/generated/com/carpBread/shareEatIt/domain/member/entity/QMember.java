package com.carpBread.shareEatIt.domain.member.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMember is a Querydsl query type for Member
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMember extends EntityPathBase<Member> {

    private static final long serialVersionUID = 1098962064L;

    public static final QMember member = new QMember("member1");

    public final com.carpBread.shareEatIt.global.entity.QBaseEntity _super = new com.carpBread.shareEatIt.global.entity.QBaseEntity(this);

    public final NumberPath<Long> accessId = createNumber("accessId", Long.class);

    public final StringPath accessToken = createString("accessToken");

    public final StringPath addressDetail = createString("addressDetail");

    public final StringPath addressSt = createString("addressSt");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isKeywordAvail = createBoolean("isKeywordAvail");

    public final BooleanPath isNoticeAvail = createBoolean("isNoticeAvail");

    public final ListPath<Keywords, QKeywords> keywordsList = this.<Keywords, QKeywords>createList("keywordsList", Keywords.class, QKeywords.class, PathInits.DIRECT2);

    public final ComparablePath<org.locationtech.jts.geom.Point> locationPoint = createComparable("locationPoint", org.locationtech.jts.geom.Point.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final StringPath nickname = createString("nickname");

    public final StringPath password = createString("password");

    public final StringPath profileImgUrl = createString("profileImgUrl");

    public final EnumPath<Provider> provider = createEnum("provider", Provider.class);

    public final StringPath refreshToken = createString("refreshToken");

    public final StringPath username = createString("username");

    public QMember(String variable) {
        super(Member.class, forVariable(variable));
    }

    public QMember(Path<? extends Member> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMember(PathMetadata metadata) {
        super(Member.class, metadata);
    }

}

