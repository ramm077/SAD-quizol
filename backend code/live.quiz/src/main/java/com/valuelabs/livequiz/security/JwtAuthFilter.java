package com.valuelabs.livequiz.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.valuelabs.livequiz.exception.CustomJwtException;
import com.valuelabs.livequiz.model.dto.response.SecurityResponseDTO;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.valuelabs.livequiz.exception.ExceptionUtility.throwInvalidResourceDetailsException;
/**
 * The JwtAuthFilter class is responsible for handling JWT authentication in the application.
 * It extends OncePerRequestFilter and filters incoming requests to validate JWT tokens.
 */
@Slf4j
@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JWTService jwtService;
    private final CustomUserDetailsService customUserDetailsService;
    @Autowired
    public JwtAuthFilter(JWTService jwtService, CustomUserDetailsService customUserDetailsService) {
        this.jwtService = jwtService;
        this.customUserDetailsService = customUserDetailsService;
    }
    /**
     * Filters incoming requests to validate JWT tokens.
     * @param request     The HTTP request.
     * @param response    The HTTP response.
     * @param filterChain The filter chain.
     * @throws ServletException If a servlet exception occurs.
     * @throws IOException      If an I/O exception occurs.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            log.info("Inside JwtAuthFilter, doFilterInternal method!");
            final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            final String jwt;
            final String username;
            if (StringUtils.isEmpty(authHeader) || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }
            jwt = authHeader.substring(7);
            username = jwtService.extractUserName(jwt);
            if (StringUtils.isNotEmpty(username) && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    log.debug("Token is Valid, fetching user details");
                    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                    log.debug("setting user details to security context");
                    UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    securityContext.setAuthentication(token);
                    SecurityContextHolder.setContext(securityContext);
                }
            }
            filterChain.doFilter(request, response);
        }
        catch (CustomJwtException exception){
            log.error("JWT TOKEN expired!, throwing Invalid resource details exception");
            throwInvalidResourceDetailsException("JWT-TOKEN","Token expired!" + exception.getMessage());
        }
        catch (RuntimeException exception){
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setContentType("application/json");
            log.error("Invalid Token Given throwing exception!");
            response.getWriter().write(convertObjectToJson(new SecurityResponseDTO("Invalid Token Given!")));
        }
    }
    /**
     * Converts an object to JSON using the Jackson ObjectMapper.
     * @param object The object to be converted.
     * @return The JSON representation of the object.
     * @throws JsonProcessingException If an error occurs during JSON processing.
     */
    public String convertObjectToJson(Object object) throws JsonProcessingException {
        if (object == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
    }
}
