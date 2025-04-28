package com.syedhamed.ecommerce.config;


import com.syedhamed.ecommerce.payload.APIResponse;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {
    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }
    @Bean
    public APIResponse apiResponse(){
        return new APIResponse();
    }
    @Bean
    public RestTemplate restTemplate(){return new RestTemplate();}

}
