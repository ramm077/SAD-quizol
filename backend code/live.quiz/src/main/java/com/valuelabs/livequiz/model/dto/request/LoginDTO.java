package com.valuelabs.livequiz.model.dto.request;

/**
 * DTO for logging in the user
 * @param emailId of the user
 * @param password of the user
 */
public record LoginDTO(String emailId, String password) {
}
