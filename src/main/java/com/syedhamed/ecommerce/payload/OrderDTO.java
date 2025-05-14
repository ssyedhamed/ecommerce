package com.syedhamed.ecommerce.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    private List<OrderItemDTO> orderItems;
    private LocalDateTime orderDate;
    private String orderStatus;
    private BigDecimal totalPrice;
    private String paymentOrderId;
}
