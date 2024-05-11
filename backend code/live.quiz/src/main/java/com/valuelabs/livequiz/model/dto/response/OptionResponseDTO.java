package com.valuelabs.livequiz.model.dto.response;

import java.util.List;

/**
 * DTO for displaying individual option based response
 * @param optionResponseId id of the option based response
 * @param chosenOptions list of chosen option ids by the user
 */
public record OptionResponseDTO(Long optionResponseId, List<Long> chosenOptions) {
}
