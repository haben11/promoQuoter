package com.kifiya.promotion_quoter.features.promotion.model;

import com.kifiya.promotion_quoter.features.promotion.enums.PromotionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@Table(name = "promotion",
        indexes = {
                @Index(name = "idx_promotion_promotion_type", columnList = "type"),
                @Index(name = "idx_promotion_promotion_active", columnList = "active")
        })
@RequiredArgsConstructor
@SQLDelete(sql = "UPDATE promotion SET deleted = true WHERE id = ?")
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private String id;

    @NotNull(message = "Promotion type must not be empty")
    @Enumerated(EnumType.STRING)
    private PromotionType type;

    private String category;

    @DecimalMin(value = "0.0", message = "Percent must be at least 0.0")
    @DecimalMax(value = "100.0", message = "Percent must be at most 100.0")
    private Double percent;

    private String productId;

    @Min(value = 1, message = "Buy Item must not be 0")
    private Integer x;

    @Min(value = 0, message = "Get free item must not be negative")
    private Integer y;

    @Min(value = 0, message = "Order priority must not be negative")
    @Column(nullable = false)
    private int orderPriority = 0;

    @Column(nullable = false)
    private boolean active = true;
    @Column(nullable = false)
    private boolean deleted = false;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Promotion promotion = (Promotion) o;
        return getId() != null && Objects.equals(getId(), promotion.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
