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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
        if(user.getAddresses().size() == 0){
            // if this the only address going to be added, make it default
            addressRequest.setDefaultAddress(true);
        }
        if (addressRequest.isDefaultAddress()) {
//            flag all other addresses as non-default
            user.getAddresses().forEach(address -> {
                if (address.isDefaultAddress()){
                    address.setDefaultAddress(false);
                }
            });
        }

        Address address = new Address();
        modelMapper.map(addressRequest, address);
        address.setUser(user);
        address.setCreatedAt(LocalDateTime.now());
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
        Long userId = authUtil.getLoggedInUserId();
        User currentUser = userRepository.findById(userId)
                .orElseThrow(()-> new UsernameNotFoundException("Unauthenticated"));
        // triggers loading within transaction to avoid LazyInitializationException
        currentUser.getAddresses().size();
        if (updatedRequest.isDefaultAddress()) {
            currentUser.getAddresses()
                    .forEach(address -> address.setDefaultAddress(false));
        }
        Address existingAddress = addressRepository.findByIdAndUser_Id(addressId, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));
        updatedRequest.setId(existingAddress.getId());
        updatedRequest.setUser(existingAddress.getUser());
        // Update only the allowed fields
        modelMapper.map(updatedRequest, existingAddress);

        return addressRepository.save(existingAddress);
    }

    @Override
    public void deleteAddressForCurrentUser(Long addressId) {
        Long userId = authUtil.getLoggedInUserId();
        User currentUser = userRepository.findById(userId)
                .orElseThrow(()-> new UsernameNotFoundException("Unauthenticated"));
        // triggers loading within transaction to avoid LazyInitializationException
        currentUser.getAddresses().size();

        Address address = addressRepository.findByIdAndUser_Id(addressId, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));
        addressRepository.delete(address);
        currentUser.getAddresses().remove(address);
        log.info("Checking if deleted address is default/not");
        if (address.isDefaultAddress() && !currentUser.getAddresses().isEmpty()) {
            log.info("The deleted address is default. Handling default address flagging from the collection....");
            handleDefaultAddressDeletion(currentUser);
        }
    }

    private void handleDefaultAddressDeletion(User currentUser){
        List<Address> addresses = currentUser.getAddresses();
        int maxUsage = -1;
        Address newDefaultAddress = null;
//        First try to find one with highest usage count
        for(Address address: addresses){
            if (address.getUsageCount() > maxUsage) {
                maxUsage = address.getUsageCount();
                newDefaultAddress = address;
            }
        }
        log.info("Max usage [{}]", maxUsage);
//        If all usage counts are 0, pick the most recently added one
        if(maxUsage == 0){
            log.info("No usage of any address. Getting the latest address...");
            newDefaultAddress = addresses.get(0);
            for(Address address : addresses){
                if(address.getCreatedAt().isAfter(newDefaultAddress.getCreatedAt())){
                    newDefaultAddress = address;
                }
            }
        }

        newDefaultAddress.setDefaultAddress(true);
        log.info("latest address created : [{}]", newDefaultAddress);
        addressRepository.save(newDefaultAddress);
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
        updatedAddressRequest.setUser(existingAddress.getUser());
        updatedAddressRequest.setCreatedAt(existingAddress.getCreatedAt());
        if(updatedAddressRequest.isDefaultAddress()){
            existingAddress.getUser().getAddresses().forEach(address -> address.setDefaultAddress(false));
        }
        // Update only the allowed fields
        modelMapper.map(updatedAddressRequest, existingAddress);
        log.info("Saving updated address: [{}]", existingAddress);
        Address address = addressRepository.save(existingAddress);
        log.info("updated address: [{}]", address);
        return address;
    }

}
