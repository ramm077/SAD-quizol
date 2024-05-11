package com.valuelabs.livequiz.model.dto.response;

import com.valuelabs.livequiz.model.enums.QuizType;

import java.util.List;

/**
 * DTO for displaying list of responses for a user for a quiz
 * @param schedulerId id of the scheduler
 * @param quizName name of the quiz
 * @param quizType type of the quiz -> TEST,OPEN_ENDED,POLL,OTHER
 * @param questionList list of question response DTO
 * @param quizScore score for the responded quiz
 * @param isAttempted whether the quiz is attempted by user
 * @param finalSubmit whether the user has submitted the quiz finally
 */
public record DisplayResponsesDTO(Long schedulerId, String quizName, QuizType quizType,
                                  List<QuestionResponseDTO> questionList, Integer quizScore,Boolean isAttempted, Boolean finalSubmit) {
}