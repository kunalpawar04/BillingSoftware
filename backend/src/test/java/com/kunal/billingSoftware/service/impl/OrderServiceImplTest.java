package com.kunal.billingSoftware.service.impl;

import com.kunal.billingSoftware.entity.OrderEntity;
import com.kunal.billingSoftware.entity.OrderItemEntity;
import com.kunal.billingSoftware.io.*;
import com.kunal.billingSoftware.repository.OrderEntityRepository;
import com.stripe.model.PaymentIntent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.awt.print.Pageable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {
    private OrderEntity order;
    private OrderRequest request;
    private String stripeSecretKey;

    @Mock
    private OrderEntityRepository orderEntityRepository;

    private PaymentVerificationRequest paymentVerificationRequest;
    private OrderServiceImpl orderService;
    private OrderRequest.OrderItemRequest itemRequest;

    @BeforeEach
    void setup() {
        orderService = new OrderServiceImpl(orderEntityRepository);

        order = OrderEntity.builder()
                .orderId("order-123")
                .paymentDetails(new PaymentDetails())
                .build();

        itemRequest = OrderRequest.OrderItemRequest.builder()
                .itemId("item-123")
                .name("Demo Item")
                .price(50.0)
                .quantity(2)
                .build();

        request = OrderRequest.builder()
                .customerName("John Doe")
                .phoneNumber("1234567890")
                .subtotal(100.0)
                .tax(10.0)
                .grandTotal(110.0)
                .paymentMethod("CASH")
                .cartItems(List.of(itemRequest))  // <-- add this
                .build();

        paymentVerificationRequest = new PaymentVerificationRequest(
                "pi_123",
                "pm_123",
                "order-123"
        );
    }

    @Test
    void testCreateOrder_WhenPaymentMethodIsCash_ShouldSetPaymentStatusCompleted() {
        // Arrange
        when(orderEntityRepository.save(any(OrderEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        OrderResponse response = orderService.createOrder(request);

        // Assert
        assertNotNull(response);
        assertEquals("John Doe", response.getCustomerName());
        assertEquals(110.0, response.getGrandTotal());
        assertEquals(PaymentMethod.CASH, response.getPaymentMethod());

        // Check payment status
        assertEquals(PaymentDetails.PaymentStatus.COMPLETED, response.getPaymentDetails().getStatus());

        // Check that order items are correctly mapped
        assertEquals(1, response.getItems().size());
        assertEquals("item-123", response.getItems().get(0).getItemId());
        assertEquals(50.0, response.getItems().get(0).getPrice());
        assertEquals(2, response.getItems().get(0).getQuantity());

        // Verify repository interaction
        verify(orderEntityRepository, times(1)).save(any(OrderEntity.class));
    }

    @Test
    void testDeleteOrder_ShouldDeleteOrder_WhenOrderIdExists() {
        // Arrange
        when(orderEntityRepository.findByOrderId(anyString())).thenReturn(Optional.of(order));

        // Act
        orderService.deleteOrder(order.getOrderId());

        // Verify
        verify(orderEntityRepository, times(1)).findByOrderId(anyString());
        verify(orderEntityRepository, times(1)).delete(order);
    }

    @Test
    void testDeleteOrder_ShouldThrowRuntimeException_WhenOrderIdDoesNotExist() {
        // Arrange
        String missingId = "missing-order-id";
        when(orderEntityRepository.findByOrderId(missingId)).thenReturn(Optional.empty());

        // Act
        RuntimeException exception = assertThrows(RuntimeException.class, () -> orderService.deleteOrder(missingId));

        // Assert + Verify
        assertEquals("Order with ID:" + missingId+ " not found", exception.getMessage());

        verify(orderEntityRepository, times(1)).findByOrderId(missingId);
        verify(orderEntityRepository, never()).delete(order);
    }

    @Test
    void testGetLatestOrders_ShouldReturnLatestOrders_WhenOrdersExist() {
        // Arrange
        order.setItems(List.of(
                OrderItemEntity.builder()
                        .itemId("item-123")
                        .name("Demo Item")
                        .price(50.0)
                        .quantity(2)
                        .build()
        ));
        order.setPaymentDetails(new PaymentDetails());
        order.setCustomerName("John Doe");
        order.setPhoneNumber("1234567890");

        when(orderEntityRepository.findAllByOrderByCreatedAtDesc())
                .thenReturn(List.of(order));

        // Act
        List<OrderResponse> responseList = orderService.getLatestOrders();

        // Assert
        assertNotNull(responseList);
        assertEquals(1, responseList.size());
        assertEquals("John Doe", responseList.get(0).getCustomerName());
        assertEquals("1234567890", responseList.get(0).getPhoneNumber());
        assertEquals(1, responseList.get(0).getItems().size());

        // Verify
        verify(orderEntityRepository, times(1)).findAllByOrderByCreatedAtDesc();
    }

    @Test
    void testGetFilteredOrder_ShouldReturnFilteredOrders_WhenCriteriaMatch() {
        // Arrange
        OrderFilterRequest filterRequest = new OrderFilterRequest();
        filterRequest.setPaymentMethod(PaymentMethod.CASH);
        filterRequest.setGrandTotal(100.0);

        OrderEntity orderEntity = OrderEntity.builder()
                .orderId("order-123")
                .customerName("John Doe")
                .phoneNumber("1234567890")
                .subtotal(90.0)
                .tax(10.0)
                .grandTotal(100.0)
                .paymentMethod(PaymentMethod.CASH)
                .paymentDetails(new PaymentDetails())
                .items(List.of(OrderItemEntity.builder()
                        .itemId("item-123")
                        .name("Demo Item")
                        .price(50.0)
                        .quantity(2)
                        .build()))
                .build();

        when(orderEntityRepository.findAll(any(Specification.class)))
                .thenReturn(List.of(orderEntity));

        // Act
        List<OrderResponse> responseList = orderService.getFilteredOrder(filterRequest);

        // Assert
        assertNotNull(responseList);
        assertEquals(1, responseList.size());
        assertEquals("John Doe", responseList.get(0).getCustomerName());
        assertEquals(PaymentMethod.CASH, responseList.get(0).getPaymentMethod());
        assertEquals(1, responseList.get(0).getItems().size());
        assertEquals("item-123", responseList.get(0).getItems().get(0).getItemId());

        // Verify
        verify(orderEntityRepository, times(1)).findAll(any(Specification.class));
    }

    @Test
    void testVerifyPayment_ShouldReturnSavedOrder_WhenPaymentIsSuccessful() throws Exception {
        // Arrange
        PaymentVerificationRequest request = new PaymentVerificationRequest(
                "pi_123",  // paymentIntentId
                "pm_123",  // paymentMethodId
                "order-123" // orderId
        );

        PaymentDetails paymentDetails = new PaymentDetails();
        OrderEntity existingOrder = OrderEntity.builder()
                .orderId("order-123")
                .paymentDetails(paymentDetails)
                .items(new ArrayList<>())
                .build();

        when(orderEntityRepository.findByOrderId("order-123"))
                .thenReturn(Optional.of(existingOrder));

        // Mock static Stripe PaymentIntent call
        try (MockedStatic<PaymentIntent> mockedStripe = mockStatic(PaymentIntent.class)) {
            PaymentIntent mockPaymentIntent = mock(PaymentIntent.class);
            when(mockPaymentIntent.getStatus()).thenReturn("succeeded");
            when(mockPaymentIntent.getId()).thenReturn("pi_123");

            mockedStripe.when(() -> PaymentIntent.retrieve("pi_123"))
                    .thenReturn(mockPaymentIntent);

            when(orderEntityRepository.save(any(OrderEntity.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            OrderResponse response = orderService.verifyPayment(request);

            // Assert
            assertNotNull(response);
            assertEquals("order-123", response.getOrderId());
            assertEquals(PaymentDetails.PaymentStatus.COMPLETED, response.getPaymentDetails().getStatus());
            assertEquals("pi_123", response.getPaymentDetails().getStripePaymentIntentId());
            assertEquals("pm_123", response.getPaymentDetails().getStripePaymentMethodId());

            // Verify repository calls
            verify(orderEntityRepository, times(1)).findByOrderId("order-123");
            verify(orderEntityRepository, times(1)).save(any(OrderEntity.class));
        }
    }

    @Test
    void testSumSalesByDate_ShouldReturnSumByDate_WhenSalesExist() {
        // Arrange
        LocalDate date = LocalDate.now();
        Double sumSalesValue = 10.00;
        when(orderEntityRepository.sumSalesByDate(date)).thenReturn(sumSalesValue);

        // Act
        var response = orderService.sumSalesByDate(date);

        // Assert
        assertNotNull(response);
        assertEquals(sumSalesValue, response);

        // Verify
        verify(orderEntityRepository, times(1)).sumSalesByDate(date);
    }

    @Test
    void testSumSalesByDate_ShouldReturnZero_WhenNoSalesExist() {
        LocalDate date = LocalDate.now();
        when(orderEntityRepository.sumSalesByDate(date)).thenReturn(null);

        Double response = orderService.sumSalesByDate(date);

        assertNull(response);
        verify(orderEntityRepository, times(1)).sumSalesByDate(date);
    }

    @Test
    void testCountOrderByDate_ShouldReturnCount_WhenOrdersExistAtGivenDate() {
        LocalDate date = LocalDate.now();
        Long countOrder = 10L;
        when(orderEntityRepository.countByOrderDate(date)).thenReturn(countOrder);

        var response = orderService.countByOrderDate(date);

        assertNotNull(response);
        assertEquals(countOrder, response);

        verify(orderEntityRepository, times(1)).countByOrderDate(date);
    }

    @Test
    void testCountOrderByDate_ShouldReturnZero_WhenOrdersDoNotExist() {
        LocalDate date = LocalDate.now();
        when(orderEntityRepository.countByOrderDate(date)).thenReturn(null);

        Long response = orderService.countByOrderDate(date);

        assertNull(response);
        verify(orderEntityRepository, times(1)).countByOrderDate(date);
    }

    @Test
    void testFindRecentOrders_ShouldReturnRecentOrders_WhenOrdersExist() {
        OrderEntity orderEntity = OrderEntity.builder()
                .orderId("order-123")
                .customerName("John Doe")
                .phoneNumber("1234567890")
                .subtotal(90.0)
                .tax(10.0)
                .grandTotal(100.0)
                .paymentMethod(PaymentMethod.CASH)
                .paymentDetails(new PaymentDetails())
                .items(List.of(OrderItemEntity.builder()
                        .itemId("item-123")
                        .name("Demo Item")
                        .price(50.0)
                        .quantity(2)
                        .build()))
                .build();

        when(orderEntityRepository.findRecentOrders(eq(PageRequest.of(0, 5)))).thenReturn(List.of(orderEntity));

        var response = orderService.findRecentOrders();

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(orderEntity.getOrderId(), response.get(0).getOrderId());
        assertEquals("John Doe", response.get(0).getCustomerName());
        assertEquals(PaymentMethod.CASH, response.get(0).getPaymentMethod());

        verify(orderEntityRepository, times(1)).findRecentOrders(eq(PageRequest.of(0, 5)));
    }

    @Test
    void testFindRecentOrders_ShouldNotReturnRecentOrders_WhenOrdersDoNotExist() {
        when(orderEntityRepository.findRecentOrders(eq(PageRequest.of(0, 5)))).thenReturn(Collections.emptyList());

        var response = orderService.findRecentOrders();

        assertNotNull(response);
        assertEquals(0, response.size());
    }
}