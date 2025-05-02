package com.syedhamed.ecommerce.model;

import com.syedhamed.ecommerce.enums.AddressType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Data
@Table(name = "addresses")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String street;
    private String city;
    private String state;
    private String pincode;
    private String country;
    @ToString.Exclude
    @Enumerated(EnumType.STRING)
    private AddressType addressType;
    private boolean defaultAddress;

    private LocalDateTime createdAt;
    private int usageCount;

//    @JsonIgnore
    @JsonBackReference
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
