package com.valuelabs.livequiz.model.dto.request;

import java.util.List;

/**
 * DTO for updating individual question by its id
 * @param questionId id of the question to be updated
 * @param questionText new text value of the question
 * @param updateOptionList list of updated option values
 */
public record UpdateQuestionDTO(Long questionId, String questionText,
                                List<UpdateOptionDTO> updateOptionList) {
}
