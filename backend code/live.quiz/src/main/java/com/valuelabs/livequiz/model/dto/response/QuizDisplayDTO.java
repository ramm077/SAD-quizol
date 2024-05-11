package com.valuelabs.livequiz.model.dto.response;

import com.valuelabs.livequiz.model.enums.QuizType;

/**
 * DTO for displaying individual quiz by its id
 * @param quizId id of the quiz
 * @param quizName name of the quiz
 * @param quizType type of the quiz -> TEST,OPEN_ENDED,POLL,OTHER
 */
public record QuizDisplayDTO(Long quizId, String quizName, QuizType quizType) {
}
