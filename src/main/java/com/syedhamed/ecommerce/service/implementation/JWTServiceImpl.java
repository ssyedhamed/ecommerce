package com.syedhamed.ecommerce.service.implementation;

import com.syedhamed.ecommerce.service.contract.JWTService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;

@Service
@Slf4j
public class JWTServiceImpl implements JWTService {
    @Value("${spring.security.secret.key}")
    private String secretKey;
    @Value("${spring.security.secret.expiration}")
    private Long expirationTime;


    @Override
    public String generateToken(String email) {
        log.info("Generating jwt token using email...");
        Date currentDate = new Date();
        Date expiredDate = new Date(currentDate.getTime() + expirationTime);

        return Jwts.builder()
                .subject(email)
                .issuer("Codesella Technologies")
                .issuedAt(new Date())
                .expiration(expiredDate)
                .signWith(buildKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    private SecretKey buildKey() {
        byte secretKeyBytes[] = secretKey.getBytes(StandardCharsets.UTF_8);
        SecretKey key = Keys.hmacShaKeyFor(secretKeyBytes);
        return key;
    }

    @Override
    public boolean isTokenValid(String token) {
        boolean isNotExpired = false;
        log.info("Checking token validity");
        Claims claims = getAllClaims(token);
        log.info("token is verified through signature using the secret key");
        if (claims != null) {
            log.info("Verifying expiration...");
            isNotExpired = claims.getExpiration().after(new Date());
            log.info(isNotExpired ? "token still has expiry" : "token is expired");

        }

        return isNotExpired ? true : false;
    }


    @Override
    public String getUsernameFromToken(String token) {
        Claims claims = getAllClaims(token);
        return claims != null ? claims.getSubject() : null;
    }


    public Claims getAllClaims(String token) {
        log.info("Verifying token using the secret key and fetching the payload...");
        Claims claims = Jwts.parser()
                .verifyWith(buildKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims;
    }

    @Override
    public Date extractExpiration(String token) {
        Claims claims = getAllClaims(token);
        return claims != null ? claims.getExpiration() : null;
    }

    @Deprecated
    @Override
    /* The token is no more fetched from request headers
    but from the cookie named 'jwt' */
    public String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        /*
        StringUtils.hasText("hello");      // true
        StringUtils.hasText("   ");        // false
        StringUtils.hasText("");           // false
        StringUtils.hasText(null);         // false
         */
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    @Override
    public String getTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null || request.getCookies().length == 0) return null;

        Cookie jwtCookie = Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals("jwt"))
                .findFirst()
                .orElse(null);
        return jwtCookie != null ? jwtCookie.getValue() : null;
    }

}
