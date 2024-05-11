package com.valuelabs.livequiz.security;
import com.valuelabs.livequiz.exception.CustomJwtException;
import com.valuelabs.livequiz.model.constants.AppConstants;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import static com.valuelabs.livequiz.model.constants.AppConstants.TOKEN_EXPIRATION_TIME;

/**
 * Service Class providing JWT Token related functionalities like Token generation and validation, and extracting all other required claims
 */
@Service
@Slf4j
public class JWTServiceImpl implements JWTService{
    private final SecretKey secretKey;
    @Lazy
    @Autowired
    public JWTServiceImpl(SecretKey secretKey) {
        this.secretKey = secretKey;
    }
    /**
     * Used to generate JWT Token using the Logged-in User details
     * @param userDetails contains the Logged-in User details
     * @return JWT token as a String
     */
    public String generateToken(UserDetails userDetails) {
        log.info("Inside JWTServiceImpl, generateToken method!");
        log.debug("generating JWT TOKEN!");
        return Jwts.builder().setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION_TIME))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    public String generateRefreshToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        log.info("Inside JWTServiceImpl, generateRefreshToken method!");
        log.debug("generating Refresh JWT TOKEN!");
        return Jwts.builder().setClaims(extraClaims).setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION_TIME))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    /**
     * Used to check whether the token is valid or not
     * @param token JWT token which is to be validated
     * @param userDetails object containing User details to be compared with the token encoded
     * @return Boolean true or false indicating whether the token is valid or not
     */
    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        log.info("Inside JWTServiceImpl, isTokenValid method!");
        log.debug("validating JWT TOKEN");
        final String username = extractUserName(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
    /**
     * Used to check whether the token is expired or not
     * @param token JWT token
     * @return Boolean true or false indicating whether the token is expired or not
     */
    private boolean isTokenExpired(String token) {
        log.info("Inside JWTServiceImpl, isTokenExpired method!");
        log.debug("Checking whether Token is expired or not");
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }
    /**
     * Used to get the Username from the JWT Token
     * @param token JWT token
     * @return Username as String
     */
    @Override
    public String extractUserName(String token) {
        log.info("Inside JWTServiceImpl, extractUserName method!");
        log.debug("Extracting User Name ");
        return extractClaim(token, Claims::getSubject);
    }
    /**
     * Used to extract a particular claim from the JWT Token
     * @param token JWT token
     * @param claimsResolvers claimResolver for instancing for extracting a Claim from JWT Token
     * @return Claims instance containing all claims extracted JWT Token
     * @param <T> Template Function to extract claims
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        log.info("Inside JWTServiceImpl, extractClaim method!");
        log.debug("Extracting Claims from the JWT Token");
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }
    /**
     * Used to extract all claims from the JWT Token
     * @param token JWT Token
     * @return Claims instancing containing all Claims
     */
    private Claims extractAllClaims(String token) {
        try {
            log.info("Inside JWTServiceImpl, extractAllClaims method");
            log.debug("Extracting All Claims from the JWT Token");
            return Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token).getBody();
        }
        catch (MalformedJwtException e){
            log.error("MalformedJwtException raised!, Invalid JWT TOKEN");
            throw new CustomJwtException("JWT-TOKEN","Invalid JWT token: + "+ HttpStatus.UNAUTHORIZED);
        }
        catch (ExpiredJwtException e){
            log.error("ExpiredJwtException raised!, JWT token is expired");
            throw new CustomJwtException("JWT-TOKEN","JWT token is expired: "+ HttpStatus.UNAUTHORIZED);
        }
        catch (UnsupportedJwtException e){
            log.error("UnsupportedJwtException raised!, JWT token is unsupported");
            throw new CustomJwtException("JWT-TOKEN","JWT token is unsupported: "+ HttpStatus.UNAUTHORIZED);
        }
        catch (IllegalArgumentException e){
            log.error("IllegalArgumentException raised!, JWT token claims String is empty");
            throw new CustomJwtException("JWT-TOKEN","JWT token claims String is empty: +"+ HttpStatus.UNAUTHORIZED);
        }
    }
    /**
     * Used to get Secret Key which is used for generating JWT Token
     * @return Key instance
     */
    private Key getSignKey() {
        log.info("Inside JWTServiceImpl, fetching the randomly generated secretKey");
        log.debug("returning the Secret Key");
        return secretKey;
    }
}
