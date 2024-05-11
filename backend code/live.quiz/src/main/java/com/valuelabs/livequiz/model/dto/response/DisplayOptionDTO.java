package com.valuelabs.livequiz.model.dto.response;

/**
 * DTO for displaying individual option by its id
 * @param optionId id of the option
 * @param optionText the text value of the option
 * @param isTrue whether the option is true or not
 */
public record DisplayOptionDTO(Long optionId, String optionText, Boolean isTrue) {
}
