package com.valuelabs.livequiz.exception;
import lombok.extern.slf4j.Slf4j;
/**
 * Utility class for throwing various custom exceptions with resource information.
 */
@Slf4j
public class ExceptionUtility {
    /**
     * Throws a ResourceNotFoundException with the specified resource name and message.
     *
     * @param resourceName The resource name associated with the exception.
     * @param message      The detail message.
     */
    public static void throwResourceNotFoundException(Object resourceName, String message) {
        log.error("throwResourceNotFoundException is used!");
        throw new ResourceNotFoundException(resourceName, message);
    }
    /**
     * Throws a ResourceExistsException with the specified resource name and message.
     *
     * @param resourceName The resource name associated with the exception.
     * @param message      The detail message.
     */
    public static void throwResourceExistsException(Object resourceName, String message) {
        log.error("throwResourceExistsException is used!");
        throw new ResourceExistsException(resourceName, message);
    }
    /**
     * Throws an InvalidResourceDetailsException with the specified resource name and message.
     *
     * @param resourceName The resource name associated with the exception.
     * @param message      The detail message.
     */
    public static void throwInvalidResourceDetailsException(Object resourceName, String message) {
        log.error("throwInvalidResourceDetailsException is used!");
        throw new InvalidResourceDetailsException(resourceName, message);
    }
}
