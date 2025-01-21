package com.carpBread.shareEatIt.config;

import com.carpBread.shareEatIt.domain.member.repository.MemberQuerydslRepository;
import com.carpBread.shareEatIt.domain.member.repository.MemberQuerydslRepositoryImpl;
import com.carpBread.shareEatIt.domain.sharingPost.repository.SharingPostQuerydslRepository;
import com.carpBread.shareEatIt.domain.sharingPost.repository.SharingPostQuerydslRepositoryImpl;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class QuerydslTestConfig {

    @PersistenceContext
    private EntityManager entityManager;

    @Bean
    public JPAQueryFactory jpaQueryFactory(){
        return new JPAQueryFactory(entityManager);
    }

    @Bean
    public MemberQuerydslRepository memberQuerydslRepository(){
        GeometryFactory geometryFactory = new GeometryFactory();
        return new MemberQuerydslRepositoryImpl(jpaQueryFactory(),geometryFactory);
    }

    @Bean
    public SharingPostQuerydslRepository sharingPostQuerydslRepository(){
        GeometryFactory geometryFactory = new GeometryFactory();
        return new SharingPostQuerydslRepositoryImpl(jpaQueryFactory(),geometryFactory);
    }
}
