package com.example.noweat.repository.menu;

import com.example.noweat.domain.menu.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MenuRepository extends JpaRepository<Menu, Long> {

    @Query("SELECT m FROM Menu m WHERE m.store.id = :storeId AND m.isDeleted = false")
    List<Menu> findMenuByStoreId(@Param("storeId") Long storeId);

    boolean existsByUser_IdAndName(Long userId, String menuName);
}
