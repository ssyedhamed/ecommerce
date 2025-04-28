package com.syedhamed.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EcommerceApp {

	public static void main(String[] args) {
		SpringApplication.run(EcommerceApp.class, args);
	}

}
