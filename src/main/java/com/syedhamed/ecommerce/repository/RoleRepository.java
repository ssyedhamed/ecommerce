package com.syedhamed.ecommerce.repository;

import com.syedhamed.ecommerce.enums.RoleName;
import com.syedhamed.ecommerce.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName roleName);
}
