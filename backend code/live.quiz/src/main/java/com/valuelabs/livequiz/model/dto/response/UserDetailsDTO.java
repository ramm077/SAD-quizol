package com.valuelabs.livequiz.model.dto.response;

import com.valuelabs.livequiz.model.enums.Role;

/**
 * DTO for displaying user details
 * @param userId id of the user
 * @param firstName of the user
 * @param lastName of the user
 * @param emailId of the user
 * @param role role of the user -> ADMIN or RESPONDER
 */
public record UserDetailsDTO(Long userId, String firstName, String lastName, String emailId, Role role) {
}
