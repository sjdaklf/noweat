package com.example.noweat.repository.order;

import com.example.noweat.domain.order.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @EntityGraph(attributePaths = {"user", "store"})
    Optional<Order> findById(Long id);

    @EntityGraph(attributePaths = "store")
    List<Order> findByUser_Id(Long userId);
}
