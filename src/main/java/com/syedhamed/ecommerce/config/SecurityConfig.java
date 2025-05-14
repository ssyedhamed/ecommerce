package com.syedhamed.ecommerce.config;

import com.syedhamed.ecommerce.security.CustomUserDetailsService;
import com.syedhamed.ecommerce.security.JWTAuthFilter;
import com.syedhamed.ecommerce.security.JWTAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebSecurity
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {



    private final CustomUserDetailsService customUserDetailsService;

    private final JWTAuthFilter jwtAuthFilter;

    private final JWTAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    SecurityFilterChain mySecurity(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(
                (requests) -> requests
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/api/auth/login", "api/users/register",
                                "api/products", "api/categories/{categoryId}/products",
                                "api/products/search").permitAll()
//                        .requestMatchers("/api/users/**", "/api/users").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/api/payment/verify").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .anyRequest().authenticated());
        //make api stateless
        http.sessionManagement(
                sessionManagementConfigurer ->
                        sessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        //disable csrf on all requests as jwt token is used for auth
        http.csrf(AbstractHttpConfigurer::disable);

        http.authenticationProvider(daoAuthenticationProvider());

        //allow h2-console frames from same origin
        http.headers(httpSecurityHeadersConfigurer ->
                httpSecurityHeadersConfigurer.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        http.exceptionHandling(
                ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint)
        );
        return http.build();
    }

//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//        return (web ->
//                web.ignoring()
//                        .requestMatchers(
//                                "/v2/api-docs",
//                                "/configuration/ui",
//                                "/swaggers-resources/**",
//                                "/configuration/security",
//                                "/swagger-ui.html",
//                                "webjars/**"));}
//

    @Bean
//    @Profile("prod")
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @SuppressWarnings("deprecation")
    @Bean
    @Profile("test")
    public PasswordEncoder passwordEncoderTest() {
        return NoOpPasswordEncoder.getInstance(); // ⚠️ Plain text, for test only!
    }

    @Bean
//    @Profile("prod")
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
//    @Profile("prod")
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }




}
