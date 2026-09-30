package com.sprint.mission.Head05_SpringServiceLayerTest.service;

import com.sprint.mission.Head05_SpringServiceLayerTest.entity.Member;
import com.sprint.mission.Head05_SpringServiceLayerTest.entity.Order;
import com.sprint.mission.Head05_SpringServiceLayerTest.entity.Product;
import com.sprint.mission.Head05_SpringServiceLayerTest.repository.DiscountPolicy;
import com.sprint.mission.Head05_SpringServiceLayerTest.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.NoSuchElementException;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @Mock private ProductRepository productRepository;
    @Mock private DiscountPolicy discountPolicy;

    @InjectMocks private OrderService orderService;

    @Test
    void vipMemberDiscountLogic() {
        // given
        Member vip = new Member("VIP");
        Product product = new Product(150000);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(discountPolicy.calculateDiscount(vip, 150000)).thenReturn(30000);

        // when
        Order order = orderService.createOrder(vip, 1L, 1);

        // then
        assertEquals(120000, order.getFinalAmount());
        assertEquals(30000, order.getDiscountAmount());
        System.out.println("=== vip 로직 테스트 완료===");
    }
}
