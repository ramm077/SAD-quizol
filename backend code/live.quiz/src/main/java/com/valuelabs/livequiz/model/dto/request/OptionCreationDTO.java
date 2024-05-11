package com.valuelabs.livequiz.model.dto.request;

/**
 * DTO for creating individual options
 * @param optionText the Text value of the option
 * @param isTrue whether the option is correct one or not
 */

public record OptionCreationDTO(String optionText, Boolean isTrue) {
}
