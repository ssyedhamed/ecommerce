package com.syedhamed.ecommerce.controllers;

import com.syedhamed.ecommerce.enums.AddressType;
import com.syedhamed.ecommerce.model.Address;
import com.syedhamed.ecommerce.payload.APIResponse;
import com.syedhamed.ecommerce.service.contract.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/addresses")
public class AddressController {
    private final AddressService addressService;
    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }

    @PostMapping
    public ResponseEntity<APIResponse<Address>> addAddress(@RequestBody @Valid Address addressRequest) {
        Address address = addressService.addAddress(addressRequest);
        return new ResponseEntity<>(new APIResponse<>(address, "Address added", true), HttpStatus.CREATED);
    }

    @GetMapping("/current-user")
    public ResponseEntity<APIResponse<List<Address>>> getAllAddressesForUser() {
        List<Address> addresses = addressService.getAllAddressesForUser();
        return ResponseEntity.ok(new APIResponse<>(addresses, "Fetched addresses", true));
    }

    @GetMapping("/{addressId}")
    public ResponseEntity<APIResponse<Address>> getAddress(@PathVariable Long addressId) {
        Address address;
        if (isAdmin()) {
            address = addressService.getAddressById(addressId); // For admin, fetch any address
        } else {
            address = addressService.getAddressByIdForCurrentUser(addressId); // For regular user, fetch only user-related address
        }
        return ResponseEntity.ok(new APIResponse<>(address, "Address found", true));
    }

    @PutMapping("/{addressId}")
    public ResponseEntity<APIResponse<Address>> updateAddress(
            @PathVariable Long addressId,
            @RequestBody Address updatedAddressRequest
    ) {
        Address updatedAddress;
        if (isAdmin()) {
            updatedAddress = addressService.updateAddressByAdmin(addressId, updatedAddressRequest);
        } else {
            updatedAddress = addressService.updateAddressForCurrentUser(addressId, updatedAddressRequest);
        }
        return ResponseEntity.ok(new APIResponse<>(updatedAddress, "Address updated successfully", true));
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<APIResponse<String>> deleteAddress(@PathVariable Long addressId) {

            addressService.deleteAddressForCurrentUser(addressId);

        return ResponseEntity.ok(new APIResponse<>("Address deleted successfully", true));
    }


    @PatchMapping("/{addressId}/default")
    public ResponseEntity<APIResponse<Address>> markAsDefault(@PathVariable Long addressId) {
        Address address;
        if(isAdmin()){
            // fetches address just by address addressId -- NEED TO UNSET DEFAULT ADDRESS ASSOCIATED TO  USER THROUGH ADDRESSES COLLECTION
            address = addressService.markAddressAsDefaultByAdmin(addressId);
        }else{
            // fetches address just by address addressId and current user -- CAN UNSET THE DEFAULT ADDRESSES WITH CURRENT USER'S ADDRESS COLLECTION
            address = addressService.markAddressAsDefaultByUser(addressId);
        }
        return ResponseEntity.ok(new APIResponse<>(address, "Address marked as default", true));
    }

    @GetMapping("/types")
    public ResponseEntity<APIResponse<List<AddressType>>> getAddressTypes() {
        List<AddressType> addressTypes = addressService.getAllAddressTypes();
        return ResponseEntity.ok(new APIResponse<>(addressTypes, "Address types retrieved successfully", true));
    }

    @GetMapping("/default")
    public ResponseEntity<APIResponse<Address>> getDefaultAddressForCurrentUser() {

        Address defaultAddress = addressService.getDefaultAddressFromCurrentUser();
        return ResponseEntity.ok(new APIResponse<>(defaultAddress, "Default address found", true));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<APIResponse<List<Address>>> getAllAddressesForUser(@PathVariable Long userId) {
        List<Address> addressesByUserId = addressService.getAddressesByUserId(userId);
        return ResponseEntity.ok(new APIResponse<>(addressesByUserId, "Addresses found", true));
    }
    @GetMapping("/all")
    public ResponseEntity<APIResponse<List<Address>>> getAllAddresses() {
        if (!isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new APIResponse<>(null, "Forbidden", false));
        }
        List<Address> allAddresses = addressService.getAllAddresses();
        return ResponseEntity.ok(new APIResponse<>(allAddresses, "All addresses retrieved", true));
    }



}
