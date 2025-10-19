package com.kunal.billingSoftware.repository;

import com.kunal.billingSoftware.entity.OrderEntity;
import com.kunal.billingSoftware.io.PaymentMethod;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class OrderEntityRepositoryTest {
    @Autowired
    private OrderEntityRepository orderEntityRepository;

    @Test
    void testFindByOrderId_WhenOrderExists() {
        OrderEntity demoOrder = OrderEntity.builder()
                .customerName("John Doe")
                .phoneNumber("9876543210")
                .subtotal(100.0)
                .tax(10.0)
                .grandTotal(110.0)
                .paymentMethod(PaymentMethod.CASH)
                .build();

        OrderEntity savedOrder = orderEntityRepository.save(demoOrder);

        Optional<OrderEntity> response = orderEntityRepository.findByOrderId(savedOrder.getOrderId());

        assertTrue(response.isPresent());
        assertEquals("John Doe", response.get().getCustomerName());
        assertEquals(110.0, response.get().getGrandTotal());
    }

    @Test
    void testFindByOrderId_WhenOrderDoesNotExist() {
        Optional<OrderEntity> response = orderEntityRepository.findByOrderId("invalid-order-id");

        assertTrue(response.isEmpty());
    }

    @Test
    void testFindAllByOrderByCreatedAtDesc_ShouldReturnOrdersInDescendingOrder() {
        // Arrange
        OrderEntity olderOrder = OrderEntity.builder()
                .customerName("Alice")
                .phoneNumber("1111111111")
                .subtotal(50.0)
                .tax(5.0)
                .grandTotal(55.0)
                .paymentMethod(PaymentMethod.CASH)
                .createdAt(LocalDateTime.now().minusDays(2))
                .build();

        OrderEntity newerOrder = OrderEntity.builder()
                .customerName("Bob")
                .phoneNumber("2222222222")
                .subtotal(100.0)
                .tax(10.0)
                .grandTotal(110.0)
                .paymentMethod(PaymentMethod.CASH)
                .createdAt(LocalDateTime.now().minusDays(1))
                .build();

        OrderEntity latestOrder = OrderEntity.builder()
                .customerName("Charlie")
                .phoneNumber("3333333333")
                .subtotal(200.0)
                .tax(20.0)
                .grandTotal(220.0)
                .paymentMethod(PaymentMethod.UPI)
                .createdAt(LocalDateTime.now())
                .build();

        orderEntityRepository.saveAll(List.of(olderOrder, newerOrder, latestOrder));

        // Act
        List<OrderEntity> result = orderEntityRepository.findAllByOrderByCreatedAtDesc();

        // Assert
        assertEquals(3, result.size());
        assertEquals("Charlie", result.get(0).getCustomerName()); // latest first
        assertEquals("Bob", result.get(1).getCustomerName());
        assertEquals("Alice", result.get(2).getCustomerName());
    }

    @Test
    void testSumSalesByDate_ShouldReturnCorrectTotal() {
        // Arrange
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        OrderEntity order1 = OrderEntity.builder()
                .customerName("Alice")
                .phoneNumber("1111111111")
                .subtotal(100.0)
                .tax(10.0)
                .grandTotal(110.0)
                .paymentMethod(PaymentMethod.CASH)
                .createdAt(now.withHour(10))
                .build();

        OrderEntity order2 = OrderEntity.builder()
                .customerName("Bob")
                .phoneNumber("2222222222")
                .subtotal(200.0)
                .tax(20.0)
                .grandTotal(220.0)
                .paymentMethod(PaymentMethod.CASH)
                .createdAt(now.withHour(14))
                .build();

        OrderEntity orderYesterday = OrderEntity.builder()
                .customerName("Charlie")
                .phoneNumber("3333333333")
                .subtotal(300.0)
                .tax(30.0)
                .grandTotal(330.0)
                .paymentMethod(PaymentMethod.UPI)
                .createdAt(now.minusDays(1))
                .build();

        orderEntityRepository.saveAll(List.of(order1, order2, orderYesterday));

        // Act
        Double totalSales = orderEntityRepository.sumSalesByDate(today);

        // Assert
        assertNotNull(totalSales);
        assertEquals(330.0, totalSales); // 110 + 220
    }

    @Test
    void testSumSalesByDate_ShouldReturnNull_WhenNoOrdersOnDate() {
        // Arrange
        LocalDate randomDate = LocalDate.of(2000, 1, 1);

        // Act
        Double totalSales = orderEntityRepository.sumSalesByDate(randomDate);

        // Assert
        assertNull(totalSales);
    }

    @Test
    void testCountByOrderDate_ShouldReturnCorrectCount() {
        // Arrange
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        OrderEntity order1 = OrderEntity.builder()
                .customerName("Alice")
                .phoneNumber("1111111111")
                .subtotal(100.0)
                .tax(10.0)
                .grandTotal(110.0)
                .paymentMethod(PaymentMethod.CASH)
                .createdAt(now.withHour(10))
                .build();

        OrderEntity order2 = OrderEntity.builder()
                .customerName("Bob")
                .phoneNumber("2222222222")
                .subtotal(200.0)
                .tax(20.0)
                .grandTotal(220.0)
                .paymentMethod(PaymentMethod.CASH)
                .createdAt(now.withHour(14))
                .build();

        OrderEntity orderYesterday = OrderEntity.builder()
                .customerName("Charlie")
                .phoneNumber("3333333333")
                .subtotal(300.0)
                .tax(30.0)
                .grandTotal(330.0)
                .paymentMethod(PaymentMethod.UPI)
                .createdAt(now.minusDays(1))
                .build();

        orderEntityRepository.saveAll(List.of(order1, order2, orderYesterday));

        // Act
        Long totalCount = orderEntityRepository.countByOrderDate(today);

        // Assert
        assertNotNull(totalCount);
        assertEquals(2, totalCount); // 110 + 220
    }

    @Test
    void testCountByOrderDate_ShouldReturnZero_WhenNoOrdersOnDate() {
        // Arrange
        LocalDate randomDate = LocalDate.of(2000, 1, 1);

        // Act
        Long totalOrders = orderEntityRepository.countByOrderDate(randomDate);

        // Assert
        assertEquals(Long.valueOf(0), totalOrders);
    }
}