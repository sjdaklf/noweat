package com.example.noweat.repository.store;

import com.example.noweat.domain.store.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoreRepository extends JpaRepository<Store, Long> {
    List<Store> findStoresByUserId(Long UserId);
}
