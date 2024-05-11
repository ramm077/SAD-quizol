package com.valuelabs.livequiz.model.dto.request;

/**
 * DTO for updating the user details
 * @param firstName of the user
 * @param lastName of the user
 * @param phoneNumber of the user
 */
public record UpdateUserDTO(String firstName, String lastName, String phoneNumber) {
}
