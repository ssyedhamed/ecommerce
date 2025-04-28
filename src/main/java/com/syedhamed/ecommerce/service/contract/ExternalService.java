package com.syedhamed.ecommerce.service.contract;

import com.syedhamed.ecommerce.payload.APIResponse;

import java.util.Map;

public interface ExternalService {
    APIResponse<Map<String, Object>> validateAddress(String pincode);
}
