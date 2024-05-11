package com.valuelabs.livequiz.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.Getter;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Custom exception indicating that a resource was not found, with an associated resource name.
 */
@Slf4j
@Getter
public class ResourceNotFoundException extends EntityNotFoundException {
    /**
     * The resource name associated with the exception.
     */
    private final Object resourceName;
    /**
     * Constructs a new ResourceNotFoundException with the specified resource name and message.
     *
     * @param resourceName The resource name associated with the exception.
     * @param message      The detail message.
     */
    public ResourceNotFoundException(Object resourceName, String message) {
        super(message);
        this.resourceName = resourceName;
    }
}
