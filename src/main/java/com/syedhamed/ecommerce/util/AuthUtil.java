package com.syedhamed.ecommerce.util;

import com.syedhamed.ecommerce.model.User;
import com.syedhamed.ecommerce.repository.UserRepository;
import com.syedhamed.ecommerce.security.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AuthUtil {

    @Autowired
    UserRepository userRepository;
    public User getAuthenticatedUserFromCurrentContext() {
        log.info("Fetching authenticated user...");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            log.info("User is not authenticated");
            throw new AccessDeniedException("User is not authenticated");
        }
        log.info("getting principal of customUserDetails...");

        CustomUserDetails customUserDetails = (CustomUserDetails) auth.getPrincipal();
        log.info("Principal fetched: [{}]", customUserDetails.getUsername());

        return customUserDetails.getUser(); //lazy
    }
    public String getLoggedInEmail(){
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        return userRepository
                .findByEmail(authentication.getName())
                .orElseThrow(()-> new UsernameNotFoundException("Unauthenticated")).getEmail();
    }

    public Long getLoggedInUserId(){
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        return userRepository
                .findByEmail(authentication.getName())
                .orElseThrow(()-> new UsernameNotFoundException("Unauthenticated")).getId();
    }

    public User getLoggedInUser(){
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        return userRepository
                .findByEmail(authentication.getName())
                .orElseThrow(()-> new UsernameNotFoundException("Unauthenticated"));
    }

}
