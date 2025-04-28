package com.syedhamed.ecommerce.repository;

import com.syedhamed.ecommerce.enums.PermissionName;
import com.syedhamed.ecommerce.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission,Long> {
    Optional<Permission> findByName(PermissionName name);
}
