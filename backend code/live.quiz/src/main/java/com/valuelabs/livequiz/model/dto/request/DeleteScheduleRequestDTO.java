package com.valuelabs.livequiz.model.dto.request;

/**
 * Deleting schedule for a quiz by admin
 * @param reason reason for deleting schedule
 */
public record DeleteScheduleRequestDTO(String reason) {
}
