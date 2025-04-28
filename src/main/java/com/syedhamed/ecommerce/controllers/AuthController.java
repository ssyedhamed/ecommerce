package com.syedhamed.ecommerce.controllers;

import com.syedhamed.ecommerce.payload.APIResponse;
import com.syedhamed.ecommerce.payload.LoginRequest;
import com.syedhamed.ecommerce.payload.LoginResponse;
import com.syedhamed.ecommerce.security.CustomUserDetails;
import com.syedhamed.ecommerce.service.contract.JWTService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api/auth")

@Slf4j
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JWTService jwtService;
    boolean isSecure = false;

    @Value("${cookie.expiration}")
    private Long cookieAge;
    @Value("${spring.profiles.active:test}")
    private String currentEnvironment;



    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authorizeUser(@RequestBody LoginRequest loginRequest) {
        log.info("login request received to /login endpoint");
        // 1. Authenticate the user
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                = new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());
        log.info("unauthenticated UsernamePasswordAuthenticationToken is created ");

        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        log.info("authenticationManager.authenticate() is passed  ");
        // 2.1 Generate JWT *only* if authentication succeeded
        String token = jwtService.generateToken(loginRequest.getEmail());
        log.info("Token Generated: " + token);
        log.info("Generating cookie...");
        isSecure = "prod".equalsIgnoreCase(currentEnvironment);
        log.info("environment " + currentEnvironment + " and cookieAge : " + cookieAge);
        //2.2 Generate Response Cookie
        ResponseCookie cookie = ResponseCookie.from("jwt")
                .httpOnly(true)
//                .maxAge(Duration.ofDays(1L))
                .maxAge(Duration.ofSeconds(cookieAge))
                .secure(isSecure) // enable in production only
                .path("/api")
                .sameSite("None")
                .value(token)
                .build();


        // 3. Build response
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        LoginResponse loginResponse = new LoginResponse();
//        @Deprecated - the token is no more shared through response - it's handled through cookies
//        loginResponse.setToken(token);
        loginResponse.setUserId(userDetails.getId());
        loginResponse.setEmail(userDetails.getUsername());
        loginResponse.setRoles(
                userDetails.getAuthorities().stream().map(
                        authority -> authority.getAuthority()
                ).toList()
        );
//        @Deprecated
//        loginResponse.setExpiresAt(expiresAt.getTime()); // milliseconds since epoch
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(loginResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity logout() {
        ResponseCookie cookie = ResponseCookie.from("jwt")
                .path("/api")
                .value("")
                .httpOnly(true)
                .sameSite("None")
                .maxAge(0)
                .build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).build();
    }

    @GetMapping("/current-user")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setUserId(userDetails.getId());
            loginResponse.setEmail(userDetails.getUsername());
            loginResponse.setRoles(
                    userDetails.getAuthorities().stream().map(
                            authority -> authority.getAuthority()
                    ).toList()
            );
            return ResponseEntity.ok(loginResponse);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new APIResponse("User is not logged in", false));
    }

}
