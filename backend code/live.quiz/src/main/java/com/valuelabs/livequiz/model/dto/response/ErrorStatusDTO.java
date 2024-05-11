package com.valuelabs.livequiz.model.dto.response;

/**
 * DTO for displaying error message for any api call
 * @param message text message to be displayed
 */
public record ErrorStatusDTO(String message) {
}
