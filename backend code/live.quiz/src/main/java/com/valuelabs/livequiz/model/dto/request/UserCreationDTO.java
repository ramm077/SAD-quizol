package com.valuelabs.livequiz.model.dto.request;

import com.valuelabs.livequiz.model.enums.Role;

/**
 * DTO for creating new user of if the inactive user exist reactivate him with new details
 * @param firstName of the user
 * @param lastName of the user
 * @param emailId of the user, must be unique
 * @param password of the user
 * @param phoneNumber of the user
 * @param role role of the user ADMIN,RESPONDER
 */
public record UserCreationDTO(String firstName, String lastName, String emailId, String password, String phoneNumber,
                              Role role) {
}
