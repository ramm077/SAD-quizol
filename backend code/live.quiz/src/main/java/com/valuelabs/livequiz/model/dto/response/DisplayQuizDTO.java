package com.valuelabs.livequiz.model.dto.response;

import com.valuelabs.livequiz.model.enums.QuizType;

import java.util.List;

/**
 * DTO for displaying individual quiz by its id
 * @param quizId id of the quiz
 * @param quizName the name of the quiz
 * @param quizType the type of the quiz TEST,OPEN_ENDED,POLL,OTHER
 * @param questionList list of Display question DTO for the quiz
 */
public record DisplayQuizDTO(Long quizId, String quizName, QuizType quizType, List<DisplayQuestionDTO> questionList) {
}
