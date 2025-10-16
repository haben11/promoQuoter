package com.kifiya.promotion_quoter.shared.utils.price;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PriceUtil {
    public static BigDecimal toCents(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
