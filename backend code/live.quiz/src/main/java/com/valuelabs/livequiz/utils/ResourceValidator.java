package com.valuelabs.livequiz.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;

import static com.valuelabs.livequiz.exception.ExceptionUtility.throwResourceNotFoundException;
/**
 * The ResourceValidator class provides a method for validating resources, such as lists of objects, based on specified
 * criteria. It includes functionality to check if a resource, identified by an entity, field, and value, exists and
 * meets certain conditions.
 */
@Slf4j
public class ResourceValidator {
    /**
     * Validates a resource based on the provided input parameters, throwing a ResourceNotFoundException if the
     * resource is not found or does not meet the specified conditions.
     * @param input    A list containing the entity, field, and value representing the resource.
     * @param inActive A Boolean flag indicating whether to check for inactive or active resources.
     */
    public static void validateResource(List<Object> input,Boolean inActive){
        Object entity = input.get(0);
        Object field = input.get(1);
        Object value = input.get(2);
        if (value instanceof CharSequence && ((CharSequence) value).isEmpty()) {
            throwResourceNotFoundException(entity, "No " + (inActive ? "inactive " : "active ") + field + " is found!");
        } else if (value instanceof Collection && ((Collection<?>) value).isEmpty()) {
            log.warn("No " + (inActive ? "inactive " : "active ") + field + " is found!");
            throwResourceNotFoundException(entity, "No " + (inActive ? "inactive " : "active ") + field + " is found!");
        }
    }

}
