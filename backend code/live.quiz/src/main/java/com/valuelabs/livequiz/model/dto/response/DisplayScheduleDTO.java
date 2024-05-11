package com.valuelabs.livequiz.model.dto.response;

import com.valuelabs.livequiz.model.enums.Role;

/**
 * DTO for displaying user details for each schedule
 * @param personId is of the user
 * @param firstName of the user
 * @param lastname of the user
 * @param emailId of the user
 * @param role role of the user ADMIN,RESPONDER
 */
public record DisplayScheduleDTO(Long personId, String firstName, String lastname, String emailId, Role role) {
}
