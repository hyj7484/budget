package com.app.application.budget.domain.enums;

public enum PaymentMethodType {
    CARD,
    CASH,
    BANK,
    TRANSIT;

    public static PaymentMethodType fromString(String type) {
        for (PaymentMethodType pmType : PaymentMethodType.values()) {
            if (pmType.name().equalsIgnoreCase(type.toUpperCase())) {
                return pmType;
            }
        }
        return null;
    }
}
