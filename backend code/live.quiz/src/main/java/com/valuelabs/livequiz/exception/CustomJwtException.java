package com.valuelabs.livequiz.exception;

import io.jsonwebtoken.JwtException;
import lombok.Getter;
/**
 * Custom exception for JWT-related errors with additional resource information.
 */
@Getter
public class CustomJwtException extends JwtException {
    /**
     * The resource name associated with the exception.
     */
    private final Object resourceName;
    /**
     * Constructs a new CustomJwtException with the specified resource name and message.
     *
     * @param resourceName The resource name associated with the exception.
     * @param message      The detail message.
     */
    public CustomJwtException(Object resourceName,String message){
        super(message);
        this.resourceName = resourceName;
    }
}