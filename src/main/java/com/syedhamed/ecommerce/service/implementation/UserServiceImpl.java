package com.syedhamed.ecommerce.service.implementation;


import com.syedhamed.ecommerce.enums.RoleName;
import com.syedhamed.ecommerce.enums.SellerApplicationStatus;
import com.syedhamed.ecommerce.enums.SellerApprovalResult;
import com.syedhamed.ecommerce.exceptions.APIException;
import com.syedhamed.ecommerce.exceptions.ResourceNotFoundException;
import com.syedhamed.ecommerce.model.Role;
import com.syedhamed.ecommerce.model.User;
import com.syedhamed.ecommerce.payload.UserAdminDTO;
import com.syedhamed.ecommerce.payload.UserAdminResponse;
import com.syedhamed.ecommerce.payload.UserRequestDTO;
import com.syedhamed.ecommerce.payload.UserResponseDTO;
import com.syedhamed.ecommerce.repository.RoleRepository;
import com.syedhamed.ecommerce.repository.UserRepository;
import com.syedhamed.ecommerce.service.contract.FileService;
import com.syedhamed.ecommerce.service.contract.PaginationService;
import com.syedhamed.ecommerce.service.contract.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final FileService fileService;
    private final PaginationService paginationService;
    private final RoleRepository roleRepository;
    @Value("${default.image.user}")
    private String DEFAULT_USER_IMAGE;

    @Override
    public Long registerUser(UserRequestDTO userRequestDTO) {
        Optional<User> userOptional = userRepository.findByEmail(userRequestDTO.getEmail());
        if (userOptional.isPresent()) {
            throw new APIException("User already exists");
        }
        User user = modelMapper.map(userRequestDTO, User.class);
        user.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
        user.setProfileImage(DEFAULT_USER_IMAGE);

        Set<Role> assignedRoles = new HashSet<>();
        if (userRequestDTO.getRoles() == null || userRequestDTO.getRoles().isEmpty()) {
            Role role = roleRepository.findByName(RoleName.ROLE_CUSTOMER)
                    .orElseThrow(() -> new ResourceNotFoundException("Role", "name", RoleName.ROLE_CUSTOMER));
            assignedRoles.add(role);
        } else {
            for (Role roleDTO : userRequestDTO.getRoles()) {
                if (roleDTO.getName() == null) {
                    throw new APIException("Role name cannot be null");
                }
                Role role = roleRepository.findByName(roleDTO.getName())
                        .orElseThrow(() -> new ResourceNotFoundException("Role", "name", roleDTO.getName().name()));
                assignedRoles.add(role);
            }

        }

        user.setRoles(assignedRoles);

        User savedUser = userRepository.save(user);
        return savedUser.getId();
    }

    @Override
    public UserResponseDTO uploadImage(MultipartFile userImage, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        if (!user.getProfileImage().equals(DEFAULT_USER_IMAGE)) {
            //delete the current image from directory
            fileService.deleteFile(user.getProfileImage());
        }
        String profileImage = fileService.saveFile(userImage);
        if (profileImage == null) {
            throw new APIException("No image is attached");
        }
        user.setProfileImage(profileImage);
        user.setUpdatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserResponseDTO.class);
    }

    @Override
//    admin scope
    @PreAuthorize("hasRole('ADMIN')")
    public UserAdminResponse getAllUsers(Integer pageNumber,
                                         Integer pageSize,
                                         String sortBy,
                                         String sortOrder) {

        Pageable usersPageable = paginationService.getPageableObject(pageNumber, pageSize, sortBy, sortOrder);
        Page<User> usersPageData = userRepository.findAllByDeletedFalse(usersPageable);
        return paginationService.getPaginatedResponse(usersPageData, UserAdminDTO.class, UserAdminResponse.class);
    }

    @Override
    public UserResponseDTO updateUser(UserRequestDTO userRequestDTO, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        modelMapper.map(userRequestDTO, user);
//       Check if the password is not null or not empty before encoding it:
//        Otherwise it will always reset the password, even when the user only wants to update their name/image.
        if (userRequestDTO.getPassword() != null && !userRequestDTO.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
        }

        user.setUpdatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserResponseDTO.class);
    }

    @Override
    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")

    public Boolean hardDeleteAllUsers() {
        List<User> softDeletedUsers = userRepository.findAllByDeletedTrue();
        userRepository.deleteAll(softDeletedUsers);
        return true;
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")

    public Boolean hardDeleteUser(Long userId) {
        getUserById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "ID: ", userId));
        userRepository.deleteById(userId);
        return true;
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")

    public UserAdminDTO restoreDeletedUser(Long userId) {
        User user = getUserById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "ID: ", userId));
        user.setDeleted(false);
        user.setEnabled(true);
        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserAdminDTO.class);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")

    public List<UserAdminDTO> getDeletedUsers() {
        List<User> softDeletedUsers = userRepository.findAllByDeletedTrue();
        return softDeletedUsers.stream().map(
                softDeleteUser -> modelMapper.map(softDeleteUser, UserAdminDTO.class)
        ).toList();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")

    public Boolean softDeleteUser(Long userId) {
        User user = userRepository.getUserByIdAndDeletedFalse(userId).orElseThrow(() -> new ResourceNotFoundException("User", "ID: ", userId));
        user.setDeleted(true);
        user.setDeletedAt(LocalDateTime.now());
        user.setEnabled(false);
        userRepository.save(user);
        return true;
    }

    @Override
    @PreAuthorize("hasAnyRole('SELLER', 'CUSTOMER')")
    //user-scope
    public Boolean deactivateUser(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        user.setDeactivated(true);
        user.setDeactivatedAt(LocalDateTime.now());
        user.setEnabled(false);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        return true;
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public SellerApprovalResult approveSellerRole(Long userId) {
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "ID: ", userId));
        SellerApplicationStatus status = user.getSellerApplicationStatus();

        switch (status) {
            case APPROVED:
                return SellerApprovalResult.ALREADY_APPROVED;
            case REJECTED:
                return SellerApprovalResult.ALREADY_REJECTED;
            case NOT_REQUESTED:
                return SellerApprovalResult.NOT_REQUESTED;
            case PENDING:
                Role role = roleRepository.findByName(RoleName.ROLE_SELLER)
                        .orElseThrow(() -> new ResourceNotFoundException("Role", "name", RoleName.ROLE_SELLER));
                user.getRoles().add(role);
                user.setSellerApplicationStatus(SellerApplicationStatus.APPROVED);
                userRepository.save(user);
                return SellerApprovalResult.APPROVED;
        }

        return SellerApprovalResult.NOT_REQUESTED; // fallback (should never hit)
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")

    public SellerApprovalResult rejectSellerRole(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "ID: ", userId));

        SellerApplicationStatus status = user.getSellerApplicationStatus();

        if (status == SellerApplicationStatus.NOT_REQUESTED) {
            return SellerApprovalResult.NOT_REQUESTED;
        }
        if (status == SellerApplicationStatus.APPROVED) {
            return SellerApprovalResult.ALREADY_APPROVED;
        }
        if (status == SellerApplicationStatus.REJECTED) {
            return SellerApprovalResult.ALREADY_REJECTED;
        }

        user.setSellerApplicationStatus(SellerApplicationStatus.REJECTED);
        userRepository.save(user);
        return SellerApprovalResult.REJECTED;
    }

    @Override
    @PreAuthorize("hasRole('CUSTOMER')")
    public SellerApprovalResult applyForSellerRole(Authentication authentication) {
        User user;
        Optional<User> userOptional = userRepository.findByEmail(authentication.getName());
        if (userOptional.isPresent()) {
            user = userOptional.get();
        } else {
            throw new ResourceNotFoundException("User", "email", authentication.getName());
        }
        // Check if already applied or approved
        SellerApplicationStatus status = user.getSellerApplicationStatus();
        switch (status) {
            case APPROVED:
                return SellerApprovalResult.ALREADY_APPROVED;
            case PENDING:
                return SellerApprovalResult.ALREADY_PENDING;
            case REJECTED:
                // If you want to allow re-applying after rejection, comment this block
                return SellerApprovalResult.ALREADY_REJECTED;
            case NOT_REQUESTED:
                user.setSellerApplicationStatus(SellerApplicationStatus.PENDING);
                userRepository.save(user);
                return SellerApprovalResult.PENDING;
            default:
                throw new APIException("Unexpected application status: " + status);
        }

    }

    @Override
    public Boolean disableUser(Long userId) {
        User user = getUserById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "ID :", userId));
        user.setEnabled(false);
        userRepository.save(user);
        return true;
    }

    @Override
    public Boolean enableUser(Long userId) {
        User user = getUserById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "ID :", userId));
        user.setEnabled(true);
        userRepository.save(user);
        return true;
    }


}
