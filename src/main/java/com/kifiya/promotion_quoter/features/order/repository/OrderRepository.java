package com.kifiya.promotion_quoter.features.order.repository;

import com.kifiya.promotion_quoter.features.order.model.OrderReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<OrderReservation, String> {
    Optional<OrderReservation> findByIdempotencyKey(String idempotencyKey);
}
