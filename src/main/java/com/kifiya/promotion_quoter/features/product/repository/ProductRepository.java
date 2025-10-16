package com.kifiya.promotion_quoter.features.product.repository;

import com.kifiya.promotion_quoter.features.product.model.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, String> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Product> findById(String s);
}
