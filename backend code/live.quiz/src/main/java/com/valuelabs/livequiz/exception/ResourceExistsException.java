package com.valuelabs.livequiz.exception;

import jakarta.persistence.EntityExistsException;
import lombok.Getter;

/**
 * Custom exception indicating that a resource already exists, with an associated resource name.
 */
@Getter
public class ResourceExistsException extends EntityExistsException {
    /**
     * The resource name associated with the exception.
     */
    private final Object resourceName;
    /**
     * Constructs a new ResourceExistsException with the specified resource name and message.
     *
     * @param resourceName The resource name associated with the exception.
     * @param message      The detail message.
     */
    public ResourceExistsException(Object resourceName, String message) {
        super(message);
        this.resourceName = resourceName;
    }
}
