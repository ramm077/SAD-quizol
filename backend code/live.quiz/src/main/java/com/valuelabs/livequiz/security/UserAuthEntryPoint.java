package com.valuelabs.livequiz.security;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.valuelabs.livequiz.model.dto.response.SecurityResponseDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
/**
 * User Authentication Entry Point which is used when some Exception is raised in the Security Filter Chain
 */
@Component
@Slf4j
public class UserAuthEntryPoint implements AuthenticationEntryPoint {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    /**
     * Method Called when an Exception is raised or thrown in the Security Filter Chain
     * @param request Servlet Request coming through the filter chain
     * @param response Servlet Response coming through the filter chain
     * @param authException AuthenticationException is thrown in case of error
     * @throws ServletException to be thrown in case of Error
     * @throws IOException to be thrown in case of Error
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.info("Inside the commence method of UserAuthEntryPoint");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        OBJECT_MAPPER.writeValue(response.getOutputStream(),
                new SecurityResponseDTO("Invalid Login credentials!"));
        log.info("Giving Response as 'Invalid Login credentials!' with status code '401'");
    }
}
