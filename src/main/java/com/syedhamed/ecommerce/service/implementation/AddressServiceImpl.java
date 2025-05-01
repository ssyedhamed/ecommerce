package com.syedhamed.ecommerce.service.implementation;

import com.syedhamed.ecommerce.enums.AddressType;
import com.syedhamed.ecommerce.exceptions.APIException;
import com.syedhamed.ecommerce.exceptions.ResourceNotFoundException;
import com.syedhamed.ecommerce.model.Address;
import com.syedhamed.ecommerce.model.User;
import com.syedhamed.ecommerce.repository.AddressRepository;
import com.syedhamed.ecommerce.repository.UserRepository;
import com.syedhamed.ecommerce.service.contract.AddressService;
import com.syedhamed.ecommerce.service.contract.ExternalService;
import com.syedhamed.ecommerce.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class AddressServiceImpl implements AddressService {
    private final AuthUtil authUtil;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final ModelMapper modelMapper;
    private final ExternalService externalService;

    @Override
    public Address addAddress(Address addressRequest) {
        String pincode = addressRequest.getPincode();
        if (!externalService.isValidAddress(pincode)) {
            throw new APIException("Enter a valid pincode");
        }
        log.info("Is Default: [{}]", addressRequest.isDefaultAddress());
        User user = authUtil.getLoggedInUser();
        if (addressRequest.isDefaultAddress()) {
            user.getAddresses().forEach(address -> {
                if (address.isDefaultAddress()){
                    address.setDefaultAddress(false);
                }
            });
        }
        Address address = new Address();
        modelMapper.map(addressRequest, address);
        address.setUser(user);
        return addressRepository.save(address);
    }


    @Override
    public List<Address> getAllAddressesForUser() {
        User user = authUtil.getAuthenticatedUserFromCurrentContext();
        return addressRepository.findByUser_Id(user.getId());
    }

    @Override
    public Address getAddressByIdForCurrentUser(Long addressId) {
        User currentUser = authUtil.getAuthenticatedUserFromCurrentContext();

        return addressRepository.findByIdAndUser_Id(addressId, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));
    }

    @Override
    public Address updateAddressForCurrentUser(Long addressId, Address updatedRequest) {
        User currentUser = authUtil.getAuthenticatedUserFromCurrentContext();
        if (updatedRequest.isDefaultAddress()) {
            currentUser.getAddresses()
                    .forEach(address -> address.setDefaultAddress(false));
        }
        Address existingAddress = addressRepository.findByIdAndUser_Id(addressId, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));

        // Update only the allowed fields
        modelMapper.map(updatedRequest, existingAddress);

        return addressRepository.save(existingAddress);
    }

    @Override
    public void deleteAddressForCurrentUser(Long addressId) {
        User currentUser = authUtil.getAuthenticatedUserFromCurrentContext();

        Address address = addressRepository.findByIdAndUser_Id(addressId, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));
        if (address.isDefaultAddress() && currentUser.getAddresses().size() > 1) {
            throw new APIException("You must assign another address as default before deleting this one.");
        }
        addressRepository.delete(address);
    }

    @Override
    public Address markAddressAsDefaultByUser(Long addressId) {
        User currentUser = authUtil.getAuthenticatedUserFromCurrentContext();

        // Ensure the address belongs to the authenticated user
        Address address = addressRepository.findByIdAndUser_Id(addressId, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));

        // Unset default address for all other addresses if this one is set as default
        if (address.isDefaultAddress()) {
            throw new APIException("This address is already marked as default.");
        }

        // Unset default address on all other addresses
        currentUser.getAddresses().forEach(addr -> addr.setDefaultAddress(false));

        // Set this address as the default address
        address.setDefaultAddress(true);

        // Save the updated address
        addressRepository.save(address);

        return address;
    }

    @Override
    public List<AddressType> getAllAddressTypes() {
        return Arrays.asList(AddressType.values());
    }

    @Override
    public Address getDefaultAddressFromCurrentUser() {
        Long userId = authUtil.getLoggedInUserId();
        User user = userRepository.findByIdWithAddresses(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // Fetch the default address
        return user.getAddresses().stream()
                .filter(Address::isDefaultAddress)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Address", "flag ", "default" ));
    }

    @Override
    public List<Address> getAddressesByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        return user.getAddresses();
    }

    @Override
    public void deleteAddressById(Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));

        addressRepository.delete(address);
    }


    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<Address> getAllAddresses() {
        return addressRepository.findAll();
    }

    @Override
    public Address getAddressById(Long addressId) {
        return addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));
    }

    @Override
    public Address markAddressAsDefaultByAdmin(Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));

        address.setDefaultAddress(true);

        // Unset the default flag for other addresses
        User user = address.getUser();
        user.getAddresses().stream()
                .filter(a -> !a.equals(address))
                .forEach(a -> a.setDefaultAddress(false));

        return addressRepository.save(address);
    }

    @Override
    public Address updateAddressByAdmin(Long addressId, Address updatedAddressRequest) {
        Address existingAddress = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));
        updatedAddressRequest.setId(existingAddress.getId());
        // Update only the allowed fields
        modelMapper.map(updatedAddressRequest, existingAddress);
        log.info("Saving updated address: [{}]", existingAddress);
        Address address = addressRepository.save(existingAddress);
        log.info("updated address: [{}]", address);
        return address;
    }

}
