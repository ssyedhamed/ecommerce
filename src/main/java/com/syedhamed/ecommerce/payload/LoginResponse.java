package com.syedhamed.ecommerce.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String email;
    private Long userId;
    private List<String> roles;
    @Deprecated
    private Long expiresAt; // milliseconds or seconds
}
