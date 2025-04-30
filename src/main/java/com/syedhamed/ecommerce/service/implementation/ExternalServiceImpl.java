package com.syedhamed.ecommerce.service.implementation;

import com.syedhamed.ecommerce.payload.external.PostalResponse;
import com.syedhamed.ecommerce.service.contract.ExternalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ExternalServiceImpl implements ExternalService {
    private final RestTemplate restTemplate;

    @Override
    public Boolean isValidAddress(String pincode) {
        String PINCODE_URI = "https://api.postalpincode.in/pincode/" + pincode;
        // Since externalResponse is an array, map to PostalResponse[]
        ResponseEntity<PostalResponse[]> externalApiResponse =
                restTemplate.exchange(PINCODE_URI, HttpMethod.GET, null, PostalResponse[].class);
        PostalResponse postalResponse = Objects.requireNonNull(externalApiResponse.getBody())[0];

        String status = postalResponse.getStatus();
        if (!status.equalsIgnoreCase("Success")) {
            return false;
        }
//        String district = postalResponse.getPostOffice().get(0).getDistrict();
//        String block = postalResponse.getPostOffice().get(0).getBlock();
//        String state = postalResponse.getPostOffice().get(0).getState();
//        List<String> areaNames = postalResponse.getPostOffice()
//                .stream().map(PostOffice::getName).toList();
        return true;
    }
}
