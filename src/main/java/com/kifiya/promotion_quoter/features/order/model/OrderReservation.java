package com.kifiya.promotion_quoter.features.order.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.proxy.HibernateProxy;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@Table(name = "order_reservation",
        indexes = {
                @Index(name = "idx_order_reservation_id", columnList = "id"),
                @Index(name = "idx_order_reservation_idempotency_key", columnList = "idempotencyKey")
        })
@RequiredArgsConstructor
@SQLDelete(sql = "UPDATE order_reservation SET deleted = true WHERE id = ?")
public class OrderReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private String id;

    @Column(unique = true)
    private String idempotencyKey;

    private BigDecimal finalPrice;
    @Column(nullable = false)
    private boolean deleted = false;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        OrderReservation order = (OrderReservation) o;
        return getId() != null && Objects.equals(getId(), order.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
