package com.syedhamed.ecommerce.payload;

import com.syedhamed.ecommerce.enums.SellerApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String profileImage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private SellerApplicationStatus sellerApplicationStatus;
}
