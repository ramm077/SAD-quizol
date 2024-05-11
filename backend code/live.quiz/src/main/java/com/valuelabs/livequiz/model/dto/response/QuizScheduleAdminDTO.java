package com.valuelabs.livequiz.model.dto.response;

/**
 * DTO for displaying schedules for quizzes by its id
 * @param schedulerId id of the schedule
 * @param startTime of the scheduled quiz
 * @param endTime of the scheduled quiz
 * @param quizId id of the quiz
 * @param quizName name of the quiz
 * @param quizType type of the quiz -> TEST,OPEN_ENDED,POLL,OTHER
 * @param questionCount count of questions in a quiz
 * @param memberCount total number of member for the scheduled quiz
 */
public record QuizScheduleAdminDTO(Long schedulerId, String startTime, String endTime, Long quizId, String quizName, String quizType, Integer questionCount,Integer memberCount) {
}
