package com.example.noweat.repository.order;

import com.example.noweat.domain.order.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @EntityGraph(attributePaths = {"user", "store"})
    Optional<Order> findById(Long id);

    @EntityGraph(attributePaths = "store")
    List<Order> findByUser_Id(Long userId);

    @EntityGraph(attributePaths = "user")
    @Query("SELECT o FROM Order o WHERE o.store.id in (SELECT s.id FROM Store s WHERE s.user.id = :userId) ORDER BY o.id")
    List<Order> findByOwnerUserId(@Param("userId") Long userId);
}
