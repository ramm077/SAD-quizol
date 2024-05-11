package com.valuelabs.livequiz.model.dto.request;

/**
 * Creating draft Schedule without any users
 * @param startTime start time of the scheduled quiz
 * @param endTime end time of the scheduled quiz
 * @param quizId the quiz id of the quiz
 */
public record DraftScheduleDTO(String startTime, String endTime, Long quizId) {
}
