package com.syedhamed.ecommerce.security;

import com.syedhamed.ecommerce.service.contract.JWTService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JWTAuthFilter extends OncePerRequestFilter {


    private final JWTService jwtService;

    private final UserDetailsService userDetailsService;
    private final JWTAuthenticationEntryPoint authenticationEntryPoint;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {

        log.info("jwt filter started...");
        String path = request.getRequestURI();
        log.info("Requested URI: [{}}", path);

        if (path.equals("/api/auth/login") || path.equals("/api/users/register")) {
            log.info("Login request won't be authenticated. Hence, passing the HttpServletRequest to subsequent filters");
            filterChain.doFilter(request, response);
            return;
        }

        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
        log.info("authentication collected from the context");

        if (currentAuth == null || !currentAuth.isAuthenticated()) {
            log.info("authentication is null");
            String token = jwtService.getTokenFromCookie(request);
            log.info("token retrieved from cookie: {}", token);

            if (token == null || token.isBlank()) {
                log.info("token is empty: Passing request to next filter");
                filterChain.doFilter(request, response);
                return;
            }

            try {
                String username = jwtService.getUsernameFromToken(token);
                if (username == null) {
                    log.warn("Username is null in token claims");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Invalid token: Username not found in claims");
                    return;
                }

                boolean isTokenValid = jwtService.isTokenValid(token);
                if (!isTokenValid) {
                    log.info("Token is invalid or expired: {}", token);
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Token is invalid or expired");
                    return;
                }

                log.info("Token is valid. Loading user details...");
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                log.info("UserDetails Fetched");
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                log.info("auth object is created");
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                log.info("Extra details applied");
                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.info("auth object is added to the security context");

            } catch (ExpiredJwtException ex) {
                log.error("Token expired", ex);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token expired");
                return;

            } catch (SignatureException ex) {
                log.error("Invalid token signature {}", ex.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid token signature");
                return;

            } catch (JwtException ex) {
                log.error("Malformed or invalid token {}", ex.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid token");
                return;

            } catch (UsernameNotFoundException ex){
                log.error("User email in Token mismatch{}", ex.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("User email in Token mismatch");
                return;
            }
            catch (BadCredentialsException ex) {
                // Log as needed (or not at all)
                logger.warn("Invalid JWT token: " + ex.getMessage());


                // Send custom response
                authenticationEntryPoint.commence(request, response, ex);

                return; // 🚫 Important: Stop filter chain here
            }
            catch (Exception ex) {
                log.error("Unexpected error during JWT processing", ex);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("Unexpected error while processing token");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }


}
