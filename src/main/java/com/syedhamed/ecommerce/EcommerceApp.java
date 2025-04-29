package com.syedhamed.ecommerce;

import com.syedhamed.ecommerce.payload.external.PostalResponse;
import com.syedhamed.ecommerce.service.contract.ExternalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
@EnableScheduling
@RequiredArgsConstructor
public class EcommerceApp {

    public static void main(String[] args) {SpringApplication.run(EcommerceApp.class, args);}
}
