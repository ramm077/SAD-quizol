package com.valuelabs.livequiz.model.dto.request;

/**
 * DTO for forgot password
 * @param emailId of the user for which the password is forgotten
 */
public record ForgotPasswordDTO(String emailId) {
}
