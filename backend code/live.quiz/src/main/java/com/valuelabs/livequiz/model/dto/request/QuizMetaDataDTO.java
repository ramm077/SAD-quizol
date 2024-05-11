package com.valuelabs.livequiz.model.dto.request;

import com.valuelabs.livequiz.model.enums.QuizType;

/**
 * DTO for changing quiz name for the quiz
 * @param quizName new name of the quiz
 */
public record QuizMetaDataDTO(String quizName) {
}
