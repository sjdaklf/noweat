package com.example.noweat.domain.store;

import com.example.noweat.domain.common.entity.BaseEntity;
import com.example.noweat.domain.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Entity
@NoArgsConstructor
@Getter
public class Store extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String name;

    private String address;

    @Enumerated(value = EnumType.STRING)
    private StoreCategory storeCategory;

    private Long minOrderPrice;

    private Double averageRating;

    private LocalTime openTime;

    private LocalTime closedTime;

    private boolean isClosed;

    private Long ratingSum;

    private Long reviewCount;

    @Builder
    public Store(User user, String name, String address, StoreCategory storeCategory, Long minOrderPrice, Double averageRating, LocalTime openTime, LocalTime closedTime, boolean isClosed, Long ratingSum, Long reviewCount) {
        this.user = user;
        this.name = name;
        this.address = address;
        this.storeCategory = storeCategory;
        this.minOrderPrice = minOrderPrice;
        this.averageRating = averageRating;
        this.openTime = openTime;
        this.closedTime = closedTime;
        this.isClosed = isClosed;
        this.ratingSum = ratingSum;
        this.reviewCount = reviewCount;
    }

    public void updateStore(String name, String address, StoreCategory storeCategory, Long minOrderPrice, LocalTime openTime, LocalTime closedTime) {
        this.name = name;
        this.address = address;
        this.storeCategory = storeCategory;
        this.minOrderPrice = minOrderPrice;
        this.openTime = openTime;
        this.closedTime = closedTime;
    }

    public void deleteStore(boolean isClosed) {
        this.isClosed = isClosed;
    }

    public void addRatingSum(Long rating){
        this.ratingSum += rating;
    }

    public void minusRatingSum(Long rating){
        this.ratingSum -= rating;
    }

    public void addReviewCount(){
        this.reviewCount++;
    }

    public void calculateAverageRating(){
        this.averageRating = (double)ratingSum/reviewCount;
    }
}