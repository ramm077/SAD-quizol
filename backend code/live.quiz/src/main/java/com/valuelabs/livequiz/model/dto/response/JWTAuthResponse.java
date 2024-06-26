package com.valuelabs.livequiz.model.dto.response;

import com.valuelabs.livequiz.model.enums.Role;

/**
 * DTO for displaying the jwt token along with user details
 * @param token generated by jwt-token authorization
 * @param userId id of the user
 * @param firstName of the user
 * @param lastName of the user
 * @param emailId of the user
 * @param role role of the user ADMIN,RESPONDER
 */
public record JWTAuthResponse(String token,Long userId, String firstName, String lastName, String emailId, Role role) {
}
