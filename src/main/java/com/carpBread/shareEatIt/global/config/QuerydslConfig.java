package com.carpBread.shareEatIt.global.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/* querydsl config */
// querydsl은 JPA위에서 작동하기 때문에 Jpaqueryfactory를 통해
// querydsl로 jpql을 작성할 수 있고, 이걸 entityManager와 연결해준다
@Configuration
public class QuerydslConfig {

    @PersistenceContext
    private EntityManager entityManager;
    @Bean
    public JPAQueryFactory jpaQueryFactory(){
        return new JPAQueryFactory(entityManager);
    }
}
