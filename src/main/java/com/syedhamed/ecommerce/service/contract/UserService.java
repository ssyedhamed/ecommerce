package com.syedhamed.ecommerce.service.contract;


import com.syedhamed.ecommerce.enums.SellerApprovalResult;
import com.syedhamed.ecommerce.model.User;
import com.syedhamed.ecommerce.payload.UserAdminDTO;
import com.syedhamed.ecommerce.payload.UserAdminResponse;
import com.syedhamed.ecommerce.payload.UserRequestDTO;
import com.syedhamed.ecommerce.payload.UserResponseDTO;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface UserService {
    //Register the user
    Long registerUser(UserRequestDTO userRequestDTO);
    // patch user image
    UserResponseDTO uploadImage(MultipartFile userImage, Authentication authentication);
    //update the user
    UserResponseDTO updateUser(UserRequestDTO userRequestDTO, Authentication authentication);
    //get all users with/without pagination --- admin scope
    UserAdminResponse getAllUsers(Integer pageNumber,
                                        Integer pageSize,
                                        String sortBy,
                                        String sortOrder);
    // get user by id - internal use
    Optional<User> getUserById(Long userId);
    //flag account as deactivated - user scope
    Boolean deactivateUser(Authentication authentication);
    // soft delete user -- admin scope
    Boolean softDeleteUser(Long userId);
    // Track deleted users
    List<UserAdminDTO> getDeletedUsers();
    // Restore deleted user
    UserAdminDTO restoreDeletedUser(Long userId);
    // hard delete one user
    Boolean hardDeleteUser(Long userId);
    //hard delete all users
    Boolean hardDeleteAllUsers();
    // mark user account as enable
    Boolean enableUser(Long userId);
    // mark user account as disable
    Boolean disableUser(Long userId);

    SellerApprovalResult applyForSellerRole(Authentication authentication);

    SellerApprovalResult approveSellerRole(Long userId);

    SellerApprovalResult rejectSellerRole(Long userId);

    //
}
