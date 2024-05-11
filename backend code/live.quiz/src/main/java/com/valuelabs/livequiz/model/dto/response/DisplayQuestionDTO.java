package com.valuelabs.livequiz.model.dto.response;

import com.valuelabs.livequiz.model.enums.QuestionType;

import java.util.List;

/**
 * DTO for displaying individual question by its id
 * @param questionId id of the question
 * @param questionText the text value of the question
 * @param questionType the type of the question -> TEXT,SINGLE,MULTIPLE
 * @param optionList list of option DTO for the question
 */
public record DisplayQuestionDTO(Long questionId, String questionText, QuestionType questionType, List<DisplayOptionDTO> optionList) {
}
