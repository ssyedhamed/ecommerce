package com.syedhamed.ecommerce.config;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class JacksonConfig {
//    Lambda-style
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> {
            builder.featuresToEnable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
            builder.failOnUnknownProperties(false);
        };
    }
//    Old-Java style
//    @Bean
//    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
//        return new Jackson2ObjectMapperBuilderCustomizer() {
//            @Override
//            public void customize(Jackson2ObjectMapperBuilder builder) {
//                builder.featuresToEnable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
//                builder.failOnUnknownProperties(false);
//            }
//        };
//    }
// could also define own custom objectMapper but not needed
//    @Bean
//    public ObjectMapper objectMapper(){
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
//        return mapper;
//    }

}
