package com.kunal.billingSoftware.specifications;

import com.kunal.billingSoftware.entity.OrderEntity;
import com.kunal.billingSoftware.entity.OrderEntity_;
import com.kunal.billingSoftware.io.PaymentMethod;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.Objects;

public class OrderSpecifications {
    public static Specification<OrderEntity> paymentMethodIs(PaymentMethod paymentMethod) {
        return (root, query, builder) -> {
            if(Objects.isNull(paymentMethod)) return null;

            return builder.equal(root.get(OrderEntity_.PAYMENT_METHOD), paymentMethod);
        };
    }

    public static Specification<OrderEntity> totalGreaterThan(Double grandTotal) {
        return (root, query, builder) -> {
            if(Objects.isNull(grandTotal)) return null;

            return builder.equal(root.get(OrderEntity_.GRAND_TOTAL), grandTotal);
        };
    }
}
