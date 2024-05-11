package com.valuelabs.livequiz.model.dto.request;

import java.util.List;

/**
 * DTO for updating schedule for a quiz by its scheduler id
 * @param startTime new start time of the scheduled quiz
 * @param endTime new end time of the scheduled quiz
 * @param addPersonIdList list of newly added person for the quiz
 * @param deletePersonIdList list of existing person ids in the schedule to be deleted
 * @param reason the reason for updating schedule
 */
public record UpdateScheduleRequestDTO(String startTime, String endTime, List<Long> addPersonIdList,List<Long> deletePersonIdList, String reason) {
}
