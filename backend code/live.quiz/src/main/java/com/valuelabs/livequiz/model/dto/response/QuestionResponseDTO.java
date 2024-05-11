package com.valuelabs.livequiz.model.dto.response;

import com.valuelabs.livequiz.model.enums.QuestionType;

import java.util.List;

/**
 * DTO for displaying individual question details
 * @param questionId id of the question
 * @param questionText text value of the question
 * @param questionType type of the question -> TEXT,SINGLE,MULTIPLE
 * @param optionList  list of display option  dto
 * @param response response DTO for the question
 * @param questionScore score for individual question
 */
public record QuestionResponseDTO(Long questionId, String questionText, QuestionType questionType,
                                  List<DisplayOptionDTO> optionList, DisplayResponseDTO response, Integer questionScore) {
}
