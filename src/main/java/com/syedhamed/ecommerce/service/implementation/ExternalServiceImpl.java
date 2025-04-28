package com.syedhamed.ecommerce.service.implementation;

import com.syedhamed.ecommerce.payload.APIResponse;
import com.syedhamed.ecommerce.payload.external.PostOffice;
import com.syedhamed.ecommerce.payload.external.PostalResponse;
import com.syedhamed.ecommerce.service.contract.ExternalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ExternalServiceImpl implements ExternalService {
    private final RestTemplate restTemplate;

    private String PINCODE_URI = "https://api.postalpincode.in/pincode/";

    @Override
    public APIResponse<Map<String, Object>> validateAddress(String pincode) {
        String url = PINCODE_URI + pincode;
        // Since externalResponse is an array, map to PostalResponse[]
        ResponseEntity<PostalResponse[]> externalApiResponse =
                restTemplate.exchange(url, HttpMethod.GET, null, PostalResponse[].class);
        PostalResponse postalResponse = externalApiResponse.getBody()[0];
        if (postalResponse != null) {
            String status = postalResponse.getStatus();
            if (!status.equalsIgnoreCase("Success")) {

//                System.out.println("No records found with pincode" + pincode);
                return new APIResponse<>("No records found with pincode " + pincode,
                        false);

            } else {
//                System.out.println("Records found with pincode" + pincode);
                String district = postalResponse.getPostOffice().get(0).getDistrict();
                String block = postalResponse.getPostOffice().get(0).getBlock();
                String state = postalResponse.getPostOffice().get(0).getState();
                Map<String, Object> internalResponse = new HashMap<>();
                internalResponse.put("district", district);
                internalResponse.put("block", block);
                internalResponse.put("state", state);
                List<String> areaNames = postalResponse.getPostOffice()
                        .stream().map(postOffice -> postOffice.getName()).toList();
                internalResponse.put("areaNames", areaNames);
                return new APIResponse<>(
                        internalResponse,
                        "Records found with pincode " + pincode,
                        true
                );
            }
        } else {
            return new APIResponse<>(
                    "Pincode Server Error",
                    false
            );
        }
    }
}
