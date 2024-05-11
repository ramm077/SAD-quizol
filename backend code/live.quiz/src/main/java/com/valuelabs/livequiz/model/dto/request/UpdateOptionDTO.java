package com.valuelabs.livequiz.model.dto.request;

/**
 * DTO for updating individual option by id
 * @param optionId  id of the option to be update
 * @param optionText new value for the option
 * @param isTrue new value for whether the option is true or not
 */
public record UpdateOptionDTO(Long optionId,String optionText,Boolean isTrue) {
}
