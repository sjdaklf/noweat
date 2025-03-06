package com.example.noweat.repository.review;

import com.example.noweat.domain.review.Review;
import com.example.noweat.domain.store.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findReviewsByUserId(Long userId);

    boolean existsByOrder_Id(Long orderId);

    List<Review> findAllByStore_IdOrderByCreatedAtDesc(Long storeId);
}
