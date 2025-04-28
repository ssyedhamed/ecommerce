package com.syedhamed.ecommerce.model;

import com.syedhamed.ecommerce.enums.PermissionName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "permissions")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = "roles")
public class Permission {
    @Id
    @GeneratedValue
    private Long id;
    @ToString.Exclude
    @Enumerated(EnumType.STRING)
    private PermissionName name;
    @ManyToMany(mappedBy = "permissions")
    @JsonIgnore
    private Set<Role> roles = new HashSet<>();
}
