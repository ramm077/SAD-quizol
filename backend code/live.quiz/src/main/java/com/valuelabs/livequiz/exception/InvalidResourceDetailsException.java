package com.valuelabs.livequiz.exception;

import lombok.Getter;
/**
 * Custom exception indicating invalid resource details with an associated resource name.
 */
@Getter
public class InvalidResourceDetailsException extends NullPointerException{
    /**
     * The resource name associated with the exception.
     */
    private final Object resourceName;
    /**
     * Constructs a new InvalidResourceDetailsException with the specified resource name and message.
     *
     * @param resourceName The resource name associated with the exception.
     * @param message      The detail message.
     */
    public InvalidResourceDetailsException(Object resourceName, String message) {
        super(message);
        this.resourceName = resourceName;
    }
}
