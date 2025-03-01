package com.example.noweat.repository.menu;

import com.example.noweat.domain.menu.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MenuRepository extends JpaRepository<Menu, Long> {

    List<Menu> findMenuByStore_Id(Long storeId);

    @Query("SELECT s FROM Menu s WHERE s.store.id = :storeId AND s.isDeleted = false")
    List<Menu> findMenuByStoreId(@Param("storeId") Long storeId);
}
