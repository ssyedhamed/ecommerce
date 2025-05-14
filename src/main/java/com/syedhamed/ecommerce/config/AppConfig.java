package com.syedhamed.ecommerce.config;


import com.syedhamed.ecommerce.payload.APIResponse;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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

//    @Bean
//    public WebMvcConfigurer corsConfigurer() {
//        return new WebMvcConfigurer() {
//            @SuppressWarnings("NullableProblems")
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//                registry.addMapping("/**")
//                        .allowedOrigins("http://localhost:3000", "http://localhost:8080") // frontend origin
//                        .allowedMethods("*")
//                        .allowedHeaders("*")
//                        .allowCredentials(true); // ⭐️ This is the key
//            }
//        };
//    }

}
