package com.example.noweat.domain.menu;

import com.example.noweat.domain.common.entity.BaseEntity;
import com.example.noweat.domain.store.Store;
import com.example.noweat.domain.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
public class Menu extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    private String menuName;

    private Long menuPrice;

    private boolean isDeleted;

    @Builder
    public Menu(Store store, User user, String menuName, Long menuPrice, boolean isDeleted) {
        this.store = store;
        this.user = user;
        this.menuName = menuName;
        this.menuPrice = menuPrice;
        this.isDeleted = isDeleted;
    }

    public void updateMenu(String menuName, Long menuPrice) {
        this.menuName = menuName;
        this.menuPrice = menuPrice;
    }

    public void deleteMenu() {
        this.isDeleted = true;
    }
}
