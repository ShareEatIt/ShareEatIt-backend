package com.carpBread.shareEatIt.domain.participation.repository;

import com.carpBread.shareEatIt.domain.participation.entity.Review;
import org.hibernate.sql.ast.tree.expression.JdbcParameter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
