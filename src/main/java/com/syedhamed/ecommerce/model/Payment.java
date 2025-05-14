package com.syedhamed.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity
@Table(name = "payment")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String paymentId;
    private String paymentOrderId;
    private String signature;
    private BigDecimal amount;
    private String currency;
    private LocalDateTime createdAt;
    private String receipt;
    @JsonIgnore
    @OneToOne
    private Order order;
}
