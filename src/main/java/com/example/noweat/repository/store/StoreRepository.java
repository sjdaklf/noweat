package com.example.noweat.repository.store;

import com.example.noweat.domain.store.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StoreRepository extends JpaRepository<Store, Long> {
    @Query ("SELECT s FROM Store s WHERE s.user.id = :userId AND s.isClosed = false")
    List<Store> findStoresByUserId(@Param("userId") Long userId);

    @Query ("SELECT s FROM Store s WHERE s.storeName LIKE %:storeName% AND s.isClosed = false")
    List<Store> findByStoreNameContaining(@Param("storeName") String storeName);

    @Query ("SELECT s FROM Store s WHERE s.isClosed = false")
    List<Store> findAllStore();
}
