package com.syedhamed.ecommerce.service.contract;

import com.syedhamed.ecommerce.enums.AddressType;
import com.syedhamed.ecommerce.model.Address;

import java.util.List;
import java.util.Map;

public interface AddressService {

    Address addAddress(Address addressRequest);
    List<Address> getAllAddressesForUser();
    Address getAddressByIdForCurrentUser(Long addressId);
    Address updateAddressForCurrentUser(Long addressId, Address updatedRequest);
    void deleteAddressForCurrentUser(Long addressId);
    Address markAddressAsDefaultByUser(Long addressId);
    List<AddressType> getAllAddressTypes();
    Address getDefaultAddressFromCurrentUser();

    // Admin-specific
    List<Address> getAddressesByUserId(Long userId);
    void deleteAddressById(Long addressId);
    List<Address> getAllAddresses();

    Address getAddressById(Long addressId);

    Address markAddressAsDefaultByAdmin(Long addressId);

    Address updateAddressByAdmin(Long addressId, Address updatedAddressRequest);

}
