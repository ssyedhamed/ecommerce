package com.syedhamed.ecommerce.security;

import com.syedhamed.ecommerce.model.User;
import com.syedhamed.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
/*
UserDetailsService is not the custom interface but
a predefined
 */
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {


    private final UserRepository userRepository;


    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("Entered CustomUserDetailsService");
        log.info("Email from jwt token received is [{}]", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        log.info("Got user from DB by email : [{}]",email);

        if (user.isDeactivated()) {
            log.info("User is deactivated");
            LocalDateTime deactivatedAt = user.getDeactivatedAt();
            Duration duration = Duration.between(deactivatedAt, LocalDateTime.now());

            // Example: Reactivate if within 30 days
            if (duration.toDays() <= 30) {
                user.setDeactivated(false);
                user.setDeactivatedAt(null);
                user.setEnabled(true);
                userRepository.save(user);  // 🔁 persist reactivation
            } else {
                throw new DisabledException("Your account was deactivated and cannot be reactivated after 30 days.");
            }
        }
        return new CustomUserDetails(user);

    }
}
