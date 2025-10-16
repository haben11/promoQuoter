package com.kifiya.promotion_quoter.features.promotion.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum PromotionType {
    PERCENT_OFF_CATEGORY("Percentage based discount applied products in a specific category"),
    BUY_X_GET_Y("For a specific product buy X units and get Y units for free");

    private String message;

    PromotionType(String message) {
        this.message = message;
    }

    @JsonValue
    public String getDescription() {
        return message;
    }

    @JsonCreator
    public static PromotionType fromString(String value) {
        return Arrays.stream(values())
                .filter(type -> type.name().equalsIgnoreCase(value)
                        || type.message.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid promotion type: " + value));
    }
}
