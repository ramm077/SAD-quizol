package com.valuelabs.livequiz.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.valuelabs.livequiz.model.dto.response.SecurityResponseDTO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
/**
 * The SecurityExceptionHandlerFilter class handles exceptions during the security filter execution.
 */
@Component
@Slf4j
public class SecurityExceptionHandlerFilter extends OncePerRequestFilter {
    /**
     * Handles the exception by setting the response status and writing the exception message as JSON.
     * @param response The HTTP servlet response.
     * @throws IOException If an error occurs during writing, the response.
     */
    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("Inside SecurityExceptionHandlerFilter : In overridden doFilterIntenal method");
        try {
            log.debug("Trying to call doFilter method of filterChain");
            filterChain.doFilter(request, response);
        } catch (RuntimeException e) {
            log.error("Error caused while executing doFilter method of filterChain. Returns a forbidden status");
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType("application/json");
            response.getWriter().write(convertObjectToJson(new SecurityResponseDTO(e.getMessage())));
        }
    }
    /**
     * Converts an object to JSON format.
     * @param object The object to be converted.
     * @return The JSON representation of the object.
     * @throws JsonProcessingException If an error occurs during JSON processing.
     */
    public String convertObjectToJson(Object object) throws JsonProcessingException {
        log.info("Inside SecurityExceptionHandlerFilter : In convertObjectToJson method");

        if (object == null) {
            log.debug("Object is null");
            return null;
        }
        log.debug("Object converted to json successfully");
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
    }
}
