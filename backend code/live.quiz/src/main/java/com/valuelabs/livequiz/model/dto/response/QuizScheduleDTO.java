package com.valuelabs.livequiz.model.dto.response;

/**
 * DTO displaying individual schedule for a quiz for the responders
 * @param schedulerId id of the schedule
 * @param startTime start of the schedule
 * @param endTime end time of the schedule
 * @param quizId id of the quiz
 * @param quizName name of the quiz
 * @param quizType type of the quiz -> TEST,OPEN_ENDED,POLL,OTHER
 * @param questionCount count of question in a quiz
 */
public record QuizScheduleDTO(Long schedulerId,String startTime, String endTime, Long quizId, String quizName, String quizType, Integer questionCount) {
}
