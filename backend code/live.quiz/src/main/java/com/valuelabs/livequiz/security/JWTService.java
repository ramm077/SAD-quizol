package com.valuelabs.livequiz.security;

import org.springframework.security.core.userdetails.UserDetails;

public interface JWTService {
    boolean isTokenValid(String token, UserDetails userDetails);

    String extractUserName(String token);
}
