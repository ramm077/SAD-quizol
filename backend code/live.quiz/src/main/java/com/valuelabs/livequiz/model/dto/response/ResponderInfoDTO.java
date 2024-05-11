package com.valuelabs.livequiz.model.dto.response;

/**
 * DTO for displaying responders to admin
 * @param personId id of the user
 * @param firstName of the user
 * @param lastName of the user
 * @param emailId of the user
 * @param phoneNumber of the user
 */
public record ResponderInfoDTO(Long personId,String firstName,String lastName,String emailId,String phoneNumber) {
}
