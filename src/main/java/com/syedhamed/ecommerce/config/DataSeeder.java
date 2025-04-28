//package com.syedhamed.ecommerce.config;
//
//import com.syedhamed.ecommerce.enums.PermissionName;
//import com.syedhamed.ecommerce.enums.RoleName;
//import com.syedhamed.ecommerce.exceptions.ResourceNotFoundException;
//import com.syedhamed.ecommerce.model.Permission;
//import com.syedhamed.ecommerce.model.Role;
//import com.syedhamed.ecommerce.repository.PermissionRepository;
//import com.syedhamed.ecommerce.repository.RoleRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.HashSet;
//import java.util.Map;
//import java.util.Set;
//import java.util.function.Function;
//import java.util.stream.Collectors;
//
//@RequiredArgsConstructor
//@Component
//@Transactional // ensure session context stays open
//public class DataSeeder implements CommandLineRunner {
//        private final RoleRepository roleRepository;
//        private final PermissionRepository permissionRepository;
//
//
//    @Override
////    public void run(String... args) throws Exception {
////// 1. Save all permissions if they don't exist
////        for(PermissionName permissionName : PermissionName.values()){
////            if (permissionRepository.findByName(permissionName).isEmpty()) {
////                Permission permission = new Permission();
////                permission.setName(permissionName);
////                permissionRepository.save(permission);
////            }
////        }
////        // 2. Fetch fresh managed permission entities from DB
////        Map<PermissionName, Permission> permissionMap = permissionRepository.findAll()
////                .stream()
////                .collect(Collectors.toMap(Permission::getName, Function.identity()));
////
////        // 3. Save roles using the managed permission instances
////        for(RoleName roleName : RoleName.values()){
////            if(roleRepository.findByName(roleName).isEmpty()){
////                Role role = new Role();
////                role.setName(roleName);
////                role.setDescription("Default description for "+roleName.name());
////                Set<Permission> permissions = new HashSet<>();
////                switch (roleName){
////                    case ROLE_ADMIN -> permissions.addAll(permissionMap.values());
////                    case ROLE_SELLER -> permissions.addAll(Set.of(
////                            permissionMap.get(PermissionName.MANAGE_PRODUCTS),
////                            permissionMap.get(PermissionName.VIEW_ORDERS)
////                    ));
////
////                    case ROLE_CUSTOMER -> permissions.addAll(Set.of(
////                            permissionMap.get(PermissionName.PLACE_ORDER),
////                            permissionMap.get(PermissionName.WRITE_REVIEW)
////                    ));
////                }
////                role.setPermissions(permissions);
////                roleRepository.save(role); // no more detached entity error!
////            }
////            }
////
////
////
////        }
//    }
//
