package com.syedhamed.ecommerce.payload.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class paymentOrderResponse {
    private String id;
    private BigDecimal amount;
    private String currency;
    @JsonProperty("created_at")
    private Long createdAt;
    private String status;
}
