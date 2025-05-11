package com.syedhamed.ecommerce.payload;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    @NotBlank(message = "The product name is required")
    @Size(min = 3, max = 50, message = "The product name should be between 3 and 50 characters")
    private String productName;
    @Size(max = 500, message = "The description should not exceed 500 characters")
    private String description;
    @Pattern(regexp = "^(?!.*\\.\\.).*$", message = "Image name cannot contain '..'")
    private String image;
    /*
    Breaking it Down:
^ → Start of the string.

This ensures that the check is applied from the beginning of the string.

(?!.*\.\.) → Negative Lookahead

(?!...) → A negative lookahead means: "fail the match if this pattern is found."

.* → Any character (.) repeated zero or more times (*).

\.\. → Matches two consecutive dots (..).

\. → Escapes the dot (since . normally matches any character).

So, (?!.*\.\.) ensures that the string is rejected if .. appears anywhere.

.* → Any characters (zero or more times).

This allows the full string to match as long as .. is not present.

$ → End of the string.

Ensures the entire string is checked, not just part of it.
     */
    @Min(value = 0, message = "Product stock should be zero or greater")
    private Integer stock;
    @PositiveOrZero(message = "Price should be zero or greater")
    private Double price;
    @Min(value = 0, message = "Discount must be between 0% and 100%")
    @Max(value = 100, message = "Discount must be between 0% and 100%")
    private Double discount;
    private Double specialPrice;
}

