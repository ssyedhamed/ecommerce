package com.syedhamed.ecommerce.payload;

import com.syedhamed.ecommerce.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDTO {
    private String email;    // unique
    private String password;
    private String firstName;
    private String lastName;
    private String phone;
    private Set<Role> roles;
}
