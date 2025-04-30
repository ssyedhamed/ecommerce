package com.syedhamed.ecommerce;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@RequiredArgsConstructor
public class EcommerceApp {
    public static void main(String[] args) {SpringApplication.run(EcommerceApp.class, args);}
}
