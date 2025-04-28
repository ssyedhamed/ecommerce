package com.syedhamed.ecommerce.payload;

import com.syedhamed.ecommerce.enums.SellerApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAdminDTO {
    private boolean accountNonLocked;
    private String email;
    private boolean emailVerified;
    private boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean deleted;
    private LocalDateTime deletedAt;
    private boolean deactivated;
    private LocalDateTime deactivatedAt;
    private SellerApplicationStatus sellerApplicationStatus;
}
