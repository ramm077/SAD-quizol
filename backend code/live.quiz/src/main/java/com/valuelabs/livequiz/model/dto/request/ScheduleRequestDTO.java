package com.valuelabs.livequiz.model.dto.request;

import java.util.List;

/**
 * DTO for creating schedule for a quiz with users
 * @param startTime of the quiz yyyy:mm:dd HH:mm:ss.sss
 * @param endTime of the quiz yyyy:mm:dd HH:mm:ss.sss
 * @param quizId id of the quiz for which schedule to be created
 * @param personIdList list of user id to whom the quiz will be scheduled to
 */
public record ScheduleRequestDTO(String startTime, String endTime, Long quizId, List<Long> personIdList) {
}
