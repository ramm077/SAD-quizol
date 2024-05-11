package com.valuelabs.livequiz.model.dto.request;

import com.valuelabs.livequiz.model.enums.QuestionType;

import java.util.List;

/**
 * DTO for capturing responses for individual
 * @param questionId id of the question
 * @param questionType type of the question
 * @param chosenOptions list of chosen option ids among all the options for a question
 * @param answerText if text based question answerText will receive Text Response
 */
public record CaptureResponseDTO(Long questionId, QuestionType questionType, List<Long> chosenOptions, String answerText) {
}
