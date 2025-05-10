package com.syedhamed.ecommerce.enums;

public enum OrderStatus {
    PENDING,       // Just created
    PROCESSING,    // Payment/inventory checks
    SHIPPED,       // Order dispatched
    DELIVERED,     // Order delivered successfully
    CANCELLED,     // Cancelled by user or admin
    FAILED         // Payment or system error
}