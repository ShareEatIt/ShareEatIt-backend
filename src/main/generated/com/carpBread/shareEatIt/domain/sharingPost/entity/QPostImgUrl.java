package com.carpBread.shareEatIt.domain.sharingPost.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPostImgUrl is a Querydsl query type for PostImgUrl
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPostImgUrl extends EntityPathBase<PostImgUrl> {

    private static final long serialVersionUID = 1064405446L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPostImgUrl postImgUrl = new QPostImgUrl("postImgUrl");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> imgOrder = createNumber("imgOrder", Integer.class);

    public final QSharingPost post;

    public final StringPath url = createString("url");

    public QPostImgUrl(String variable) {
        this(PostImgUrl.class, forVariable(variable), INITS);
    }

    public QPostImgUrl(Path<? extends PostImgUrl> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPostImgUrl(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPostImgUrl(PathMetadata metadata, PathInits inits) {
        this(PostImgUrl.class, metadata, inits);
    }

    public QPostImgUrl(Class<? extends PostImgUrl> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.post = inits.isInitialized("post") ? new QSharingPost(forProperty("post"), inits.get("post")) : null;
    }

}

