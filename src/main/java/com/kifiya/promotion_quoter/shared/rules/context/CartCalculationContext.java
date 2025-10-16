package com.kifiya.promotion_quoter.shared.rules.context;

import com.kifiya.promotion_quoter.features.cart.dto.request.CartItem;
import com.kifiya.promotion_quoter.features.product.model.Product;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class CartCalculationContext {
    private List<CartItem> items;
    private Map<String, Product> products;
    private Map<String, BigDecimal> currentPrices = new HashMap<>();
    private List<String> auditTrail = new ArrayList<>();

    public void addAudit(String description) {
        auditTrail.add(description);
    }
}
