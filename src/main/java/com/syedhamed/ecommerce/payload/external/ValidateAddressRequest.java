package com.syedhamed.ecommerce.payload.external;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidateAddressRequest {
    @NotBlank(message = "Pincode must not be blank")
    @Pattern(regexp = "\\d{6}", message = "Pincode must be a 6-digit number")
    private String pincode;
}
