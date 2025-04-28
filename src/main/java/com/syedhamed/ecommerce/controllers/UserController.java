package com.syedhamed.ecommerce.controllers;


import com.syedhamed.ecommerce.config.AppConstants;
import com.syedhamed.ecommerce.enums.SellerApprovalResult;
import com.syedhamed.ecommerce.payload.*;
import com.syedhamed.ecommerce.service.contract.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {



    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Map<String,Long>> registerUser(@RequestBody UserRequestDTO userRequestDTO){
        Long userId = userService.registerUser(userRequestDTO);
        log.info("New user registered with ID: {}", userId);

        return ResponseEntity.ok(Map.of("userId", userId));
    }

    @PatchMapping("/image")
    public ResponseEntity<UserResponseDTO> uploadImage(@RequestParam(value = "image") MultipartFile userImage,
                                                       Authentication authentication){
        log.info("User {} uploading a new profile image...", authentication.getName());
        UserResponseDTO userResponseDTO = userService.uploadImage(userImage, authentication);
        log.info("User {} uploaded a new profile image successfully", authentication.getName());
        return ResponseEntity.ok(userResponseDTO);
    }

    @PutMapping("/apply-seller")
    public ResponseEntity<APIResponse> applyForSellerRole(Authentication authentication){
        String userEmail = authentication.getName();  // for logging through user email
        SellerApprovalResult result = userService.applyForSellerRole(authentication);
        // Internal logging for debugging / audit
        log.info("Seller application result for user {}: {}", authentication.getName(), result);

        // Log based on the outcome
        switch (result) {
            case PENDING -> log.info("User [{}] submitted a seller application successfully.", userEmail);
            case ALREADY_APPROVED -> log.warn("User [{}] attempted to apply for seller role but is already approved.", userEmail);
            case ALREADY_PENDING -> log.warn("User [{}] attempted to reapply for seller role while already pending.", userEmail);
            case ALREADY_REJECTED -> log.warn("User [{}] attempted to reapply after rejection.", userEmail);
            case NOT_REQUESTED -> log.error("User [{}] encountered an unexpected NOT_REQUESTED status.", userEmail);
            default -> log.error("User [{}] resulted in unexpected status: {}", userEmail, result);
        }

        return switch (result) {
            case PENDING -> ResponseEntity.ok(new APIResponse("Your seller application has been submitted for review.", true));
            // All other cases hidden behind a generic message for security
            case ALREADY_APPROVED, ALREADY_PENDING, ALREADY_REJECTED, NOT_REQUESTED ->
                    ResponseEntity.badRequest().body(new APIResponse("Unable to process your request at this time.", false));
            default -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new APIResponse("Unexpected status occurred.", false));
        };
    }

    @PutMapping("/{userId}/approve-seller")
    public ResponseEntity<APIResponse> approveSellerRequest(@PathVariable Long userId) {
        SellerApprovalResult result = userService.approveSellerRole(userId);


        // Log internal result for auditing/debugging
        log.info("Admin approval attempt for userId {}: {}", userId, result);

        if (result == SellerApprovalResult.APPROVED) {
            log.info("Seller application approved for userId {}", userId);
            return ResponseEntity.ok(new APIResponse("Seller application approved.", true));
        } else {
            log.warn("Approval failed for userId {}: {}", userId, result);
            return ResponseEntity.badRequest().body(new APIResponse("Unable to approve the seller application.", false));
        }
    }

    @PutMapping("/{userId}/reject-seller")
    public ResponseEntity<APIResponse> rejectSellerRequest(@PathVariable Long userId) {
        SellerApprovalResult result = userService.rejectSellerRole(userId);

        // Log result for internal tracking
        log.info("Admin rejection attempt for userId {}: {}", userId, result);

        if (result == SellerApprovalResult.REJECTED) {
            log.info("Seller application rejected for userId {}", userId);
            return ResponseEntity.ok(new APIResponse("Seller application has been rejected.", true));
        } else {
            log.warn("Rejection failed for userId {}: {}", userId, result);
            return ResponseEntity.badRequest().body(new APIResponse("Unable to reject the seller application.", false));
        }
    }


    @PutMapping("")
    public ResponseEntity<UserResponseDTO> updateUser(
                                                      @RequestBody UserRequestDTO userRequestDTO,
                                                      Authentication authentication){
        UserResponseDTO userResponseDTO = userService.updateUser(userRequestDTO, authentication);
        return ResponseEntity.ok(userResponseDTO);
    }

    @GetMapping
    //admin-scope
    public ResponseEntity<UserAdminResponse> getAllUsers(
            @RequestParam(name = "pageNumber", required = false , defaultValue = AppConstants.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(name = "pageSize", required = false, defaultValue = AppConstants.PAGE_SIZE) Integer pageSize,
            @RequestParam(name = "sortBy", required = false, defaultValue = "id") String sortBy,
            @RequestParam(name = "sortOrder", required = false, defaultValue = AppConstants.SORT_ORDER) String sortOrder

    ){
        UserAdminResponse allUsers = userService.getAllUsers(pageNumber, pageSize, sortBy, sortOrder);
        return  ResponseEntity.ok(allUsers);
    }

    @GetMapping("/deleted")
    public ResponseEntity<List<UserAdminDTO>> getSoftDeletedUsers(){
        List<UserAdminDTO> deletedUsers = userService.getDeletedUsers();
        return ResponseEntity.ok(deletedUsers);
    }

    @PatchMapping("/deactivate")
    //user-scope
    public ResponseEntity<Map<String, String>> deactivateUser(Authentication authentication){
        userService.deactivateUser(authentication);
        return ResponseEntity.ok(Map.of("message","User has been deactivated"));
    }

    @DeleteMapping("/{userId}")
    //admin-scope
    public ResponseEntity<Map<String,String>> softDeleteUser(@PathVariable Long userId){
        userService.softDeleteUser(userId);
        return ResponseEntity.ok(Map.of("message","User has been moved to trash"));
    }

    @PatchMapping("/{userId}/restore")
    //admin-scope
    public ResponseEntity<UserAdminDTO> restoreDeletedUser(@PathVariable Long userId){
        return ResponseEntity.ok(userService.restoreDeletedUser(userId));
    }

    @DeleteMapping("/{userId}/purge")
    //admin-scope
    public ResponseEntity<Map<String,String>> hardDeleteUser(@PathVariable Long userId){
        log.info("Hard delete triggered for userId {}", userId);
        userService.hardDeleteUser(userId);
        return ResponseEntity.ok(Map.of("message", "User has been deleted permanently"));
    }

    @DeleteMapping("purge-all")
    //admin-scope
    public ResponseEntity<Map<String,String>> hardDeleteAllUsers(){
        log.warn("Hard delete triggered for ALL users!"); // stronger logging
        userService.hardDeleteAllUsers();
        return ResponseEntity.ok(Map.of("message","All Users have been deleted permanently"));
    }


}
