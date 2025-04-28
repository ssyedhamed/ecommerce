package com.syedhamed.ecommerce.service.contract;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;

import java.util.Date;

public interface JWTService {

    String generateToken(String email);

    boolean isTokenValid(String token);

    String getUsernameFromToken(String token);

     Claims getAllClaims(String token);

    Date extractExpiration(String token);

    String getTokenFromRequest(@NonNull HttpServletRequest request);

    String getTokenFromCookie(HttpServletRequest request);
}
