package com.example.noweat.repository.review;

import com.example.noweat.domain.review.Review;
import com.example.noweat.domain.store.Store;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findReviewsByUserId(Long userId);

    boolean existsByOrder_Id(Long orderId);

    List<Review> findAllByStore_IdOrderByCreatedAtDesc(Long storeId);

    @Query(value = "SELECT * FROM review WHERE star_rating BETWEEN :minRating AND :maxRating ORDER BY created_at DESC", nativeQuery = true)
    List<Review> findAllByMinRatingAndMaxRating(@Param("minRating") Integer minRating, @Param("maxRating") Integer maxRating);

    @EntityGraph(attributePaths = {"user", "store", "order"})
    Optional<Review> findById(Long id);
}
