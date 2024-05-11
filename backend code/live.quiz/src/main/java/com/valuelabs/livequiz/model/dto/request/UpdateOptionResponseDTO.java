package com.valuelabs.livequiz.model.dto.request;

import java.util.List;

/**
 * DTO for updating individual question response by its id
 * @param questionId id of the question to be updated
 * @param responseId response id of the question
 * @param optionResponseId response id of the individual option
 * @param chosenOptions list of newly chosen options
 */
public record UpdateOptionResponseDTO(Long questionId,Long responseId, Long optionResponseId, List<Long> chosenOptions) {
}
