package com.example.noweat.repository.store;

import com.example.noweat.domain.store.Store;
import com.example.noweat.domain.store.StoreCategory;
import com.example.noweat.domain.user.User;
import com.example.noweat.domain.user.UserRole;
import com.example.noweat.repository.user.UserRepository;
import com.example.noweat.service.exception.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class StoreRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StoreRepository storeRepository;

    @Test
    @DisplayName("사용자 아이디로 가게 조회")
    void findStoresByUserId() {

        // given
        User user = User.builder()
                .email("a@naver.com")
                .password("1234")
                .address("서울시 중구")
                .name("홍길동")
                .userRole(UserRole.OWNER)
                .storeCount(2L)
                .isDeleted(false)
                .build();

        userRepository.save(user);

        List<Store> stores = new ArrayList<>();

        Store store1 = Store.builder()
                .user(user)
                .name("가게1")
                .address("서울시 강남구")
                .storeCategory(StoreCategory.KOREAN)
                .minOrderPrice(10000L)
                .averageRating(4.5)
                .openTime(LocalTime.of(9, 0))
                .closedTime(LocalTime.of(22, 0))
                .isClosed(false)
                .build();
        stores.add(store1);

        Store store2 = Store.builder()
                .user(user)
                .name("가게2")
                .address("서울시 강북구")
                .storeCategory(StoreCategory.JAPANESE)
                .minOrderPrice(5000L)
                .averageRating(4.8)
                .openTime(LocalTime.of(8, 0))
                .closedTime(LocalTime.of(23, 0))
                .isClosed(false)
                .build();
        stores.add(store2);

        storeRepository.save(store1);
        storeRepository.save(store2);

        // when
        List<Store> findStores = storeRepository.findStoresByUserId(user.getId());

        // then
        assertNotNull(findStores);
        assertEquals(2, findStores.size());
        assertEquals(stores.get(0).getName(), findStores.get(0).getName());
    }

    @Test
    @DisplayName("가게 이름으로 조회")
    void findByStoreNameContaining() {

        // given
        User user = User.builder()
                .email("a@naver.com")
                .password("1234")
                .address("서울시 중구")
                .name("홍길동")
                .userRole(UserRole.OWNER)
                .storeCount(3L)
                .isDeleted(false)
                .build();

        userRepository.save(user);

        List<Store> stores = new ArrayList<>();

        Store store1 = Store.builder()
                .user(user)
                .name("김밥천국")
                .address("서울시 강남구")
                .storeCategory(StoreCategory.KOREAN)
                .minOrderPrice(10000L)
                .averageRating(4.5)
                .openTime(LocalTime.of(9, 0))
                .closedTime(LocalTime.of(22, 0))
                .isClosed(false)
                .build();
        stores.add(store1);

        Store store2 = Store.builder()
                .user(user)
                .name("스시집")
                .address("서울시 강북구")
                .storeCategory(StoreCategory.JAPANESE)
                .minOrderPrice(5000L)
                .averageRating(4.8)
                .openTime(LocalTime.of(8, 0))
                .closedTime(LocalTime.of(23, 0))
                .isClosed(false)
                .build();
        stores.add(store2);

        Store store3 = Store.builder()
                .user(user)
                .name("김밥이야기")
                .address("서울시 금천구")
                .storeCategory(StoreCategory.KOREAN)
                .minOrderPrice(10000L)
                .averageRating(5.0)
                .openTime(LocalTime.of(8, 0))
                .closedTime(LocalTime.of(20, 0))
                .isClosed(false)
                .build();
        stores.add(store3);

        storeRepository.save(store1);
        storeRepository.save(store2);
        storeRepository.save(store3);

        // when
        List<Store> findStore = storeRepository.findByStoreNameContaining("김밥");

        // then
        assertNotNull(findStore);
        assertEquals(2, findStore.size());
        assertTrue(findStore.get(0).getName().contains("김밥"));
    }

    @Test
    @DisplayName("전체 가게 조회")
    void findAllStore() {

        // given
        User user = User.builder()
                .email("a@naver.com")
                .password("1234")
                .address("서울시 중구")
                .name("홍길동")
                .userRole(UserRole.OWNER)
                .storeCount(2L)
                .isDeleted(false)
                .build();

        userRepository.save(user);

        List<Store> stores = new ArrayList<>();

        Store store1 = Store.builder()
                .user(user)
                .name("김밥천국")
                .address("서울시 강남구")
                .storeCategory(StoreCategory.KOREAN)
                .minOrderPrice(10000L)
                .averageRating(4.5)
                .openTime(LocalTime.of(9, 0))
                .closedTime(LocalTime.of(22, 0))
                .isClosed(false)
                .build();
        stores.add(store1);

        Store store2 = Store.builder()
                .user(user)
                .name("스시집")
                .address("서울시 강북구")
                .storeCategory(StoreCategory.JAPANESE)
                .minOrderPrice(5000L)
                .averageRating(4.8)
                .openTime(LocalTime.of(8, 0))
                .closedTime(LocalTime.of(23, 0))
                .isClosed(false)
                .build();
        stores.add(store2);

        storeRepository.save(store1);
        storeRepository.save(store2);

        // when
        List<Store> findStores = storeRepository.findAllStore();

        // then
        assertNotNull(findStores);
        assertEquals(2, findStores.size());
        assertEquals(stores.get(0).getName(), findStores.get(0).getName());
    }

    @Test
    @DisplayName("스토어 아이디로 가게 조회")
    void findById() {

        // given
        User user = User.builder()
                .email("a@naver.com")
                .password("1234")
                .address("서울시 중구")
                .name("홍길동")
                .userRole(UserRole.OWNER)
                .storeCount(1L)
                .isDeleted(false)
                .build();

        userRepository.save(user);

        Store store = Store.builder()
                .user(user)
                .name("김밥천국")
                .address("서울시 강남구")
                .storeCategory(StoreCategory.KOREAN)
                .minOrderPrice(10000L)
                .averageRating(4.5)
                .openTime(LocalTime.of(9, 0))
                .closedTime(LocalTime.of(22, 0))
                .isClosed(false)
                .build();

        storeRepository.save(store);

        // when
        Optional<Store> findStore = storeRepository.findById(store.getId());

        // then
        assertNotNull(findStore);
        assertEquals(store.getName(), findStore.get().getName());
    }

}